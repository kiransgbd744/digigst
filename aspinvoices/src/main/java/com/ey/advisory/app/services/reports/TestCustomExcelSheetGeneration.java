/**
 * 
 */
package com.ey.advisory.app.services.reports;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import com.aspose.cells.ICellsDataTable;
import com.aspose.cells.ImportTableOptions;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.ey.advisory.app.data.views.client.ReversalComputeDto;
import com.google.common.collect.Lists;

/**
 * @author Sujith.Nanga
 *
 * 
 */
public class TestCustomExcelSheetGeneration {

	static class CustomCellsDataTable implements ICellsDataTable {

		// This is the current row index
		int m_index = -1;

		// These are your column names
		String[] colsNames;// = new String[] { "Pet", "Fruit", "Country",
							// "Color" };

		// These are the data of each column
		String[] col0data = new String[] { "Dog", "Cat", "Duck" };
		String[] col1data = new String[] { "Apple", "Pear", "Banana" };
		String[] col2data = new String[] { "UK", "USA", "China" };
		String[] col3data = new String[] { "Red", "Green", "Blue" };

		// Combine all of the data into a single two dimensional array
		String[][] colsData = new String[][] { col0data, col1data, col2data,
				col3data };

		List<ReversalComputeDto> list;

		/**
		 * @param list
		 */
		public CustomCellsDataTable(List<ReversalComputeDto> list) {
			this.list = list;

			Map<String, List<ReversalComputeDto>> gstinMap = list.stream()
					.collect(Collectors
							.groupingBy(ReversalComputeDto::getGstin));

			colsNames = new String[gstinMap.size() + 1];
			colsNames[0] = "Taxable Supplies";
			// These are your column names
			int headerColumn = 1;
			List<String> gstinList = Lists.newArrayList(gstinMap.keySet());
			for (int header = 0; header < gstinList.size(); header++) {
				colsNames[headerColumn] = ("Amount - " + gstinList.get(header));
				headerColumn++;
			}
			
			col0data = new String[16];
			for (int column = 0; column < gstinList.size(); column++) {
				List<ReversalComputeDto> dataList = gstinMap.get(gstinList.get(column));
				for(ReversalComputeDto dto:dataList) {
					col0data[column] = dto.getTaxbleValue();
				}
				column++;
			}

		}

		public void beforeFirst() {
			m_index = -1;
		}

		public Object get(int columnIndex) {

			Object o = null;
			o = colsData[columnIndex][m_index];
			return o;
		}

		public Object get(String columnName) {
			return null;
		}

		public String[] getColumns() {
			return colsNames;
		}

		public int getCount() {
			return col0data.length;
		}

		public boolean next() {
			m_index++;
			return true;
		}
	}

	public static void main(String[] args) {

		// List<ReversalComputeDto> list = Lists.newArrayList(
		// new ReversalComputeDto("ABC", "TAX", "1231.00"),
		// new ReversalComputeDto("ABC", "B", "2311"),
		// new ReversalComputeDto("ABC", "C", "3232"),
		// new ReversalComputeDto("DEF", "TAX", "2311"));
		//
		// CustomCellsDataTable table = new CustomCellsDataTable(list);
		//
		// System.out.println(table.colsData);
		// System.out.println(table.colsNames);

		String srcDir = "C:\\Users\\sujith.nanga\\Desktop\\01-08-2020\\";
		String outDir = "C:\\Users\\sujith.nanga\\Desktop\\01-08-2020\\";

		List<ReversalComputeDto> list = Lists.newArrayList(
				new ReversalComputeDto("ABC", "TAX", "1231.00"),
				new ReversalComputeDto("ABC", "B", "2311"),
				new ReversalComputeDto("ABC", "C", "3232"),
				new ReversalComputeDto("DEF", "TAX", "2311"));

		// Create the instance of Cells Data Table
		CustomCellsDataTable cellsDataTable = new CustomCellsDataTable(list);

		// Load the sample workbook
		Workbook wb;
		try {
			wb = new Workbook(srcDir + "input.xlsx");
			// Access first worksheet
			Worksheet ws = wb.getWorksheets().get(0);

			// Import data table options
			ImportTableOptions opts = new ImportTableOptions();

			// We do now want to shift the first row down when inserting rows.
			opts.setShiftFirstRowDown(false);
			ws.autoFitColumns();

			// Import cells data table
			ws.getCells().importData(cellsDataTable, 0, 1, opts); // row, column

			// Save the workbook
			wb.save(outDir + "input.xlsx");
			System.out.println("Excel created...");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
