package com.ey.advisory.common;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * This class contains the utility methods to align similar collections and 
 * perform the required operation. Sometimes, we encounter scenarios where we 
 * have collections containing elements of the same data type, but the size of
 * these collections are different. If these different collections can be 
 * aligned by columns by inserting nulls in relevant places, then the resulting
 * list of aligned collections can be used to  perform column level functions
 * that return a collection of the same size as one of the input collections.
 * 
 * @author Divya1.B
 *
 */
public class CollectionAlignmentUtil {

	public static <T, U extends Comparable<? super U>> List<U> extractKeys(
			List<List<T>> lists, Function<T, U> extractorFn) {
		return lists.stream().flatMap(e -> e.stream())
				.map(e -> extractorFn.apply(e)).distinct().sorted()
				.collect(Collectors.toList());
	}

	public static <T> Collector<T, ?, T> toSingleton() {
		return Collectors.collectingAndThen(Collectors.toList(), list -> {
			if (list.size() != 1) {
				throw new IllegalStateException();
			}
			return list.get(0);
		});
	}

	public static <T, U extends Comparable<? super U>> List<List<T>> alignLists(
			List<List<T>> lists, Function<T, U> extractorFn) {

		List<U> allKeys = extractKeys(lists, extractorFn);
		List<Map<U, T>> lookups = lists.stream().map(e -> {
			return e.stream()
					.collect(Collectors.groupingBy(extractorFn, toSingleton()));
		}).collect(Collectors.toList());
		List<List<T>> alignedLists = lookups.stream()
				.map(m -> alignList(m, allKeys)).collect(Collectors.toList());

		return alignedLists;
	}

	public static <T, U extends Comparable<? super U>, V> List<V> 
				alignListsAndTransform(
					List<List<T>> lists, Function<T, U> extractorFn,
					Function<List<T>, V> transformFn) {
		List<List<T>> alignedLists = alignLists(lists, extractorFn);
		List<U> allKeys = extractKeys(lists, extractorFn);
		return IntStream.range(0, allKeys.size()).boxed()
				.map(idx -> getListAtIndex(alignedLists, idx))
				.map(e -> transformFn.apply(e)).collect(Collectors.toList());
	}

	private static <T> List<T> getListAtIndex(List<List<T>> lists,
			Integer idx) {
		return lists.stream().map(lst -> lst.get(idx))
				.collect(Collectors.toList());
	}

	private static <T, U> List<T> alignList(Map<U, T> lookup, List<U> keys) {
		return keys.stream().map(k -> lookup.get(k))
				.collect(Collectors.toList());
	}

}
