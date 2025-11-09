package com.avereon.zerra;

import com.avereon.product.Product;
import com.avereon.product.ProductCard;
import com.avereon.product.Rb;
import com.avereon.settings.Settings;
import lombok.experimental.Accessors;

import java.nio.file.Path;

@Accessors(fluent=true)
public class MockProduct implements Product {

	private final Product parent;

	private final String rbPath;

	public MockProduct() {
		this( null, null );
	}

	public MockProduct( String rbPath ) {
		this( null, rbPath );
	}

	public MockProduct( Product parent, String rbPath ) {
		this.parent = parent;
		this.rbPath = rbPath;
		Rb.init( this );
	}

	@Override
	public ProductCard getCard() {
		return new ProductCard();
	}

	@Override
	public Settings getSettings() {
		return null;
	}

	@Override
	public Path getDataFolder() {
		return null;
	}

	public String getRbText( String rbKey, String valueKey ) {
		return Rb.textOr( this, rbKey, valueKey, null );
	}

}
