package jaywalker.ant;

import jaywalker.classlist.*;
import jaywalker.util.ResourceLocator;
import jaywalker.util.Shell;
import jaywalker.report.AggregateReport;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;

import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;

public class JayWalkerTask extends Task {

    public static class Classlist extends FileSet {
    }

    private File tempDir;
    protected Vector classlists = new Vector();
    private File output;

    protected void validate() {
        if (output == null) {
            throw new BuildException("report output not set");
        }
        if (classlists.size() < 1) {
            throw new BuildException("classlist not set");
        }
    }

    public void execute() {
        validate();

        try {
            String classlist = createClasslist();
            getProject().setNewProperty("classlist", classlist);
            String tempPath = (tempDir != null) ? tempDir.getAbsolutePath() : "";
            ResourceLocator.instance().register("tempDir", Shell.toWorkingDir(tempPath));
            ResourceLocator.instance().register("classlistElementCache", new ClasslistElementCache());

            final ClasslistElementFactory factory = new ClasslistElementFactory();
            final ClasslistElement [] elements = factory.create(classlist);
            ClasslistElementVisitor visitor = new ClasslistElementVisitor(elements);
            final ClasslistElementStatistic statisticListener = new ClasslistElementStatistic();
            visitor.addListener(statisticListener);
            AggregateReport report = new AggregateReport();
            visitor.addListener(report);
            Date start = new Date();
            visitor.accept(visitor);
            System.out.println("Time to visit elements : " + (new Date().getTime() - start.getTime()));
            System.out.println(statisticListener + " instrumented.");
            BufferedWriter out = new BufferedWriter(new FileWriter(output));
            out.write(report.toString());
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new BuildException(e);
        }

    }

    public void setTempDir(File tempDir) {
        this.tempDir = tempDir;
    }

    public void addClasslist(Classlist classlist) {
        classlists.add(classlist);
    }

    private String createClasslist() {
        StringBuffer sb = new StringBuffer();
        for (Iterator itFilesets = classlists.iterator(); itFilesets.hasNext();) {
            FileSet fs = (FileSet) itFilesets.next();
            DirectoryScanner ds = fs.getDirectoryScanner(getProject());
            String[] includedFiles = ds.getIncludedFiles();
            for (int i = 0; i < includedFiles.length; i++) {
                sb.append(new File(ds.getBasedir(), includedFiles[i]).getAbsolutePath());
                sb.append(File.pathSeparator);
            }
        }
        return sb.toString();
    }

    public void setOutput(File output) {
        this.output = output;
    }

}
