package nz.ac.massey.cs.gql4jung.jmpl;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class Logging {
	static Logger LOG_GQL = Logger.getLogger(GQLImpl.class);
	static Logger LOG_BIND = Logger.getLogger(Bindings.class);
	static Logger LOG_INST = Logger.getLogger(MotifInstanceImpl.class);
	static {
		BasicConfigurator.configure();
	}
	Logging() {
		super();
		
		// debugging
		Level level = Level.ERROR;
		// runtime
		LOG_GQL.setLevel(level);
		LOG_BIND.setLevel(level);
		LOG_INST.setLevel(level);
	}
}
