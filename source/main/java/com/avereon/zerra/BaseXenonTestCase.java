package com.avereon.zerra;

import com.avereon.util.OperatingSystem;
import com.avereon.xenon.Program;
import org.junit.jupiter.api.BeforeEach;

import java.util.logging.Level;

public class BaseXenonTestCase {

	protected Program program;

	@BeforeEach
	protected void setup() throws Exception {
		// Be sure that the OperatingSystem class is properly set
		OperatingSystem.reset();
		program = new Program();
		java.util.logging.Logger.getLogger( "" ).setLevel( Level.OFF );
	}

	protected Program getProgram() {
		return program;
	}

}
