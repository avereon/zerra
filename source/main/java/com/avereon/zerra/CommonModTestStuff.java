package com.avereon.zerra;

import com.avereon.product.ProductCard;
import com.avereon.xenon.Module;
import com.avereon.xenon.Xenon;

import static org.assertj.core.api.Assertions.assertThat;

public interface CommonModTestStuff {

	static Module initMod( Xenon program, ProductCard card ) {
		Module module = program.getProductManager().getMod( card.getProductKey() );

		program.getProductManager().setModEnabled( card, true );
		assertThat( program.getProductManager().isEnabled( card ) ).withFailMessage( "Module not ready for testing: " + module ).isTrue();

		module.init( program, ProductCard.card( module ) );

		return module;
	}

}
