package com.avereon.zerra;

import com.avereon.product.Product;
import com.avereon.product.Rb;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

public record Option<T>(T key, String name) {

	@NonNull
	@Override
	public String toString() {
		return name;
	}

	public static <T> List<Option<T>> of( Product product, String bundle, T[] options ) {
		List<Option<T>> optionList = new ArrayList<>();
		for( T option : options ) {
			String prefix = option.getClass().getSimpleName().toLowerCase() + "-";
			String name = prefix + option.toString().toLowerCase().replace( '_', '-' );
			optionList.add( new Option<>( option, Rb.text( product, bundle, name ) ) );
		}
		return optionList;
	}

}
