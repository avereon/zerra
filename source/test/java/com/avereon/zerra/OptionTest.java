package com.avereon.zerra;

import com.avereon.product.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class OptionTest {

	private Product product;

	@BeforeEach
	void setup() {
		product = new MockProduct();
	}

	@Test
	void ofString() {
		List<Option<String>> options = Option.of( product, "tool", new String[]{ "A", "B", "C" } );
		assertThat( options ).containsExactly( new Option<>( "A", "Letter A" ), new Option<>( "B", "Letter B" ), new Option<>( "C", "Letter C" ) );
	}

	@Test
	void ofEnum() {
		List<Option<TestEnum>> options = Option.of( product, "tool", TestEnum.values() );
		assertThat( options ).containsExactly( new Option<>( TestEnum.ONE, "one" ), new Option<>( TestEnum.TWO, "two" ), new Option<>( TestEnum.THREE, "three" ) );
	}

	private enum TestEnum {
		ONE,
		TWO,
		THREE
	}

}
