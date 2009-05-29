package nz.ac.massey.cs.gql4jung.jmpl;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class Logging {
	static Logger LOG_GQL = Logger.getLogger(GQLImpl.class);
	static Logger LOG_BIND = Logger.getLogger(Bindings.class);
	static Logger LOG_INST = Logger.getLogger(MotifInstanceImpl.class);
	static Logger LOG_SCHED = Logger.getLogger(ConstraintScheduler.class);
	static Logger LOG_CACHE = Logger.getLogger(LRUCache.class);
	static {
		BasicConfigurator.configure();
	}
	Logging() {
		super();
		
		// debugging
		Level level = Level.WARN;
		//Level level = Level.DEBUG;
		// runtime
		LOG_GQL.setLevel(level);
		LOG_BIND.setLevel(level);
		LOG_INST.setLevel(level);
		LOG_SCHED.setLevel(level);
		LOG_CACHE.setLevel(level);
	}
}
