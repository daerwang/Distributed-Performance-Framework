package org.tc.perf.work.items;

import static org.tc.perf.util.SharedConstants.HOSTNAME;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.tc.perf.process.ProcessState;
import org.tc.perf.util.Configuration;
import org.tc.perf.util.FileLoader;

/**
 * 
 * Starts cleanup process. gzips all the specified logs and uploads them to the
 * cache to be downloaded on MasterControl node.
 * 
 * @author Himadri Singh
 */
public class CleanupL2 extends AbstractWork {

	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(CleanupL2.class);

	public CleanupL2(final Configuration configuration) {
		super(configuration);
	}

	@Override
	public ProcessState work() {
		FileLoader loader = new FileLoader(getDataCache());
		String clientLogLocation = configuration.getLocation() + "server";
		List<Pattern> patterns = new ArrayList<Pattern>();
		for (String p : configuration.getLogRegex())
			patterns.add(Pattern.compile(p));

		List<File> dirs = new ArrayList<File>();
		dirs.add(new File(clientLogLocation));
		dirs.add(new File(clientLogLocation + "/logs"));

		ProcessState state = new ProcessState();
		try {
			File gzip = new File("server-" + HOSTNAME + ".tar.gz");
			loader.gzipFiles(dirs, patterns, gzip);
			loader.uploadSingleFile(gzip);
			gzip.delete();
		} catch (Exception e) {
			state.markFailed();
			state.setFailureReason(e.getMessage());
			return state;
		}
		state.markFinished();
		log.info("Cleanup completed. Ready for next task.");
		return state;
	}

}
