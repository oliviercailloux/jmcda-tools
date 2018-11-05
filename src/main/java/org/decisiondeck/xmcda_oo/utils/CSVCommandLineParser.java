package org.decisiondeck.xmcda_oo.utils;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.decisiondeck.jmcda.exc.InvalidInvocationException;

class CSVCommandLineParser {

    public CSVCommandLineParser() {
	m_options = null;
	m_inputFile = null;
	m_outputFile = null;
	m_overwrite = false;
    }

    private Options m_options;
    private String m_inputFile;

    /**
     * @return <code>null</code> if not yet parsed, not <code>null</code> after a successful parsing.
     */
    public File getInputFile() {
	return m_inputFile == null ? null : new File(m_inputFile);
    }

    /**
     * @return <code>null</code> if not yet parsed, not <code>null</code> after a successful parsing.
     */
    public File getOutputFile() {
	final String out;
	if (m_outputFile == null) {
	    if (m_inputFile == null) {
		out = null;
	    } else {
		final String fileExt = "\\...?.?\\z";
		final String base = m_inputFile.replaceAll(fileExt, "");
		// if (m_inputFile.endsWith(".csv")) {
		// base = m_inputFile.substring(0, m_inputFile.length() - 4);
		// } else {
		// base = m_inputFile;
		// }
		out = base + ".xml";
	    }
	} else {
	    out = m_outputFile;
	}

	return out == null ? null : new File(out);
    }

    private String m_outputFile;
    private boolean m_overwrite;

    public Options getCommandLineOptions() {
	if (m_options == null) {
	    final Option optIn = OptionBuilder.create("i");
	    optIn.setArgs(1);
	    optIn.setRequired(true);
	    optIn.setDescription("The CSV input file.");
	    optIn.setArgName("inputFile");
	    optIn.setLongOpt("inputFile");
	    optIn.setType("File");
	    final Option optOut = OptionBuilder.create("o");
	    optOut.setArgs(1);
	    optOut.setRequired(false);
	    optOut.setDescription("The path to the file to be written.");
	    optOut.setArgName("outputFile");
	    optOut.setLongOpt("outputFile");
	    optOut.setType("File");
	    final Option optOverwrite = OptionBuilder.create("w");
	    optOverwrite.setArgs(0);
	    optOverwrite.setRequired(false);
	    optOverwrite.setDescription("Overwrite any existing file without confirmation.");
	    optOverwrite.setArgName("overwrite");
	    optOverwrite.setLongOpt("overwrite");
	    optOverwrite.setType("boolean");

	    m_options = new Options();
	    m_options.addOption(optIn);
	    m_options.addOption(optOut);
	    m_options.addOption(optOverwrite);
	}
	return m_options;
    }

    public void parse(String[] args) throws InvalidInvocationException {
	CommandLine line;
	try {
	    line = new GnuParser().parse(getCommandLineOptions(), args);
	    m_inputFile = line.getOptionValue("i");
	    m_outputFile = line.getOptionValue("o");
	    m_overwrite = line.hasOption("w");
	} catch (ParseException exc) {
	    throw new InvalidInvocationException(exc);
	}
    }

    public boolean getOverwrite() {
	return m_overwrite;
    }

    public String getSyntaxHelp() {
	final HelpFormatter hlp = new HelpFormatter();
	final StringWriter wr = new StringWriter();
	final PrintWriter pwr = new PrintWriter(wr);
	hlp.printHelp(pwr, hlp.getWidth(), "prg", null, getCommandLineOptions(), hlp.getLeftPadding(),
		hlp.getDescPadding(), null, true);
	return wr.toString();
    }

}