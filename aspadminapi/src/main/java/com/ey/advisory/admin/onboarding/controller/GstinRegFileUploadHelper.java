package com.ey.advisory.admin.onboarding.controller;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.admin.services.onboarding.gstinfileupload.GstinRegObjArrToEntityConverter;
import com.ey.advisory.admin.services.onboarding.gstinfileupload.docs.GstinRegFileUploadService;
import com.ey.advisory.admin.services.onboarding.gstinfileupload.docs.OnboardingFileTraverserFactory;
import com.ey.advisory.app.util.HeaderCheckerUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.eyfileutils.tabular.DummyTabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataSourceTraverser;
import com.ey.advisory.common.eyfileutils.tabular.impl.FileUploadDocRowHandler;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Service("GstinRegFileUploadHelper")
public class GstinRegFileUploadHelper {

    private static final String[] EXPECTED_HEADERS = { "GSTIN",
            "Registration type", "Registered Name", "GSTN Username",
            "Effective Date of Registration", "eMail Registered with GSTN",
            "Mobile Number Registered with GSTN",
            "Primary Authorized Signatory eMail",
            "Secondary Authorized Signatory eMail", "Primary Contact eMail",
            "Secondary Contact eMail", "Bank A/C No", "Turnover", "Address 1",
            "Address 2", "Address 3" };

    private static final Logger LOGGER = LoggerFactory
            .getLogger(GstinRegFileUploadHelper.class);

    @Autowired
    @Qualifier("GstinRegObjArrToEntityConverter")
    private GstinRegObjArrToEntityConverter converter;

    @Autowired
    @Qualifier("DefaultGstinRegFileUploadService")
    private GstinRegFileUploadService gstinRegService;

    @Autowired
    @Qualifier("OnboardingDefaultTraverserFactoryImpl")
    private OnboardingFileTraverserFactory onboardingFileTraverserFactory;

    @Autowired
    @Qualifier("HeaderCheckerUtil")
    private HeaderCheckerUtil headerCheckerUtil;

    public ResponseEntity<String> gstinRegUpload(
            @RequestParam("file") MultipartFile[] files, String groupCode,
            Long entityId) throws Exception {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("GstinRegFileUploadHelper gstinRegUpload begin");
        }
        try {

            // Assuming that the multi-part request will have only one part.
            // Hence, directly accessing the first element.
            MultipartFile file = files[0];

            // Get the uploaded file name and a reference to the input stream of
            // the uploaded file.
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());

            InputStream stream = file.getInputStream();

            TabularDataSourceTraverser traverser = onboardingFileTraverserFactory
                    .getTraverser(fileName);

            TabularDataLayout layout = new DummyTabularDataLayout(16);

            // Add a dummy row handler that will keep counting the rows.
            @SuppressWarnings("rawtypes")
            FileUploadDocRowHandler rowHandler = new FileUploadDocRowHandler<>();
            traverser.traverse(stream, layout, rowHandler, null);
            Object[] getHeaders = rowHandler.getHeaderRow();
            List<Object[]> list = rowHandler.getFileUploadList();
            removeNullRecords(list, 16);

            Pair<Boolean, String> pair = headerCheckerUtil
                    .validateHeaders(EXPECTED_HEADERS, getHeaders);

            // Convert the object array to domain objects.
            if (pair.getValue0()) {
                Pair<List<String>, List<GSTNDetailEntity>> filePairs = converter
                        .convert(list, groupCode, entityId);
                List<GSTNDetailEntity> value1 = filePairs.getValue1();
                Set<String> dupcheck = new HashSet<>();
                for (GSTNDetailEntity entity : value1) {
                    if (!dupcheck.add(entity.getGstin())) {
                        Gson gson = GsonUtil.newSAPGsonInstance();
                        APIRespDto dto = new APIRespDto("Failed",
                                "Repeated gstin in uploaded excel sheet please recheck"
                                        + " and upload with correct data.");
                        JsonObject resp = new JsonObject();
                        JsonElement respBody = gson.toJsonTree(dto);
                        resp.add("resp", respBody);
                        return new ResponseEntity<>(resp.toString(),
                                HttpStatus.OK);
                    }
                }

                List<GSTNDetailEntity> saveElgbleEntities = new ArrayList<>();
                filePairs.getValue1().forEach(entity -> {
                    List<Long> ids = gstinRegService
                            .getByGstin(entity.getGstin(), entityId);
                    if (ids != null && ids.size() > 0) {
                        // Assuming list has only one active record
                        entity.setId(ids.get(0));
                    }
                    saveElgbleEntities.add(entity);
                });

                List<String> errors = filePairs.getValue0();
                if (errors.isEmpty() || errors.size() == 0) {
                    List<GSTNDetailEntity> reordsToBeInserted = filePairs
                            .getValue1();
                    if (reordsToBeInserted != null
                            && !reordsToBeInserted.isEmpty()) {
                        gstinRegService.disableUploadedGstinEntries(entityId,
                                groupCode, saveElgbleEntities);
                        gstinRegService.createGstinEntriesInOrg(entityId,
                                groupCode, saveElgbleEntities);

                        gstinRegService.saveAll(saveElgbleEntities);
                    }
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug(
                                "GstinRegFileUploadHelper gstinRegUpload end");
                    }
                    return createGstinRegSuccessResp();
                } else {
                    return createGstinRegFailureResp(errors);
                }
            } else {
                return createInvalidTemplateGstinRegFailureResp();
            }

        } catch (DataIntegrityViolationException e) {
            Gson gson = GsonUtil.newSAPGsonInstance();
            APIRespDto dto = new APIRespDto("Failed",
                    "Repeated data in uploaded excel sheet please recheck"
                            + " and upload with correct data.");
            JsonObject resp = new JsonObject();
            JsonElement respBody = gson.toJsonTree(dto);
            resp.add("resp", respBody);
            LOGGER.error("Exption Occred: {}", e);
            return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
        } catch (Exception e) {
            Gson gson = GsonUtil.newSAPGsonInstance();
            APIRespDto dto = new APIRespDto("Failed", e.getMessage());
            JsonObject resp = new JsonObject();
            JsonElement respBody = gson.toJsonTree(dto);
            resp.add("resp", respBody);
            LOGGER.error("Exption Occred: {}", e);
            return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
        }
    }

    private void removeNullRecords(List<Object[]> list, int count) {
        List<Object[]> matchedNullList = new ArrayList<Object[]>();
        list.forEach(obj -> {
            List<Object> nullList = Arrays.asList(obj).stream()
                    .filter(val -> val == null).collect(Collectors.toList());
            if (Arrays.asList(obj).contains(null) && nullList.size() == count) {
                matchedNullList.add(obj);
            }
        });
        list.removeAll(matchedNullList);
    }

    public ResponseEntity<String> createGstinRegFailureResp(
            List<String> errors) {
        Gson gson = GsonUtil.newSAPGsonInstance();
        String message = "File uploaded failed due to errors->" + errors;
        APIRespDto dto = new APIRespDto("Failed", message);
        JsonObject resp = new JsonObject();
        JsonElement respBody = gson.toJsonTree(dto);
        resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
        resp.add("resp", respBody);
        return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
    }

    public ResponseEntity<String> createGstinRegSuccessResp() {
        Gson gson = GsonUtil.newSAPGsonInstance();
        APIRespDto dto = new APIRespDto("Success",
                "File uploaded successfully check your status in Registration Tab..");
        JsonObject resp = new JsonObject();
        JsonElement respBody = gson.toJsonTree(dto);
        resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
        resp.add("resp", respBody);
        return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
    }

    public ResponseEntity<String> createInvalidTemplateGstinRegFailureResp() {
        Gson gson = GsonUtil.newSAPGsonInstance();
        APIRespDto dto = new APIRespDto(" Upload Failed ", "Invalid template,"
                + " Please upload again with correct template.");
        JsonObject resp = new JsonObject();
        JsonElement respBody = gson.toJsonTree(dto);
        String msg = "Unexpected error while uploading  files";
        resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
        resp.add("resp", respBody);
        return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
    }

    // This method uses the uploaded file name to determine the appropriate
    // traverser.
    public List<Object[]> readFromStream(String fileName, InputStream stream) {

        // Get the appropriate traverser based on the file type.
        TabularDataSourceTraverser traverser = onboardingFileTraverserFactory
                .getTraverser(fileName);
        TabularDataLayout layout = new DummyTabularDataLayout(16);

        // Add a dummy row handler that will keep counting the rows.
        FileUploadDocRowHandler rowHandler = new FileUploadDocRowHandler();
        long stTime = System.currentTimeMillis();
        traverser.traverse(stream, layout, rowHandler, null);

        return rowHandler.getFileUploadList();
    }
}
