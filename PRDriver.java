import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.mapreduce.Counters;
import org.apache.hadoop.mapreduce.Counter;

import java.io.IOException;
import java.util.*;

/*
netID: ws392
rejectMin: 0.2637
rejectLimit: 0.2737
Number of node after filter: 685031
*/
public class PRDriver {

	public static final float TOTALNODES = 685229f;// 685031f; says wesley //?685229 nodes; 679814 lines

	public static enum PRCounters {
		residualChange
	};

	public static final int MAX_ITERATIONS = 6; // 	MAXIMUN iterations to run
	public static final float DECIMAL_ACCURACY = 100000;

	public static void main(String[] args) throws Exception {

		if(args.length !=2) {
			System.err.println("Error in passing arguments: PRDriver <input directory> <output path>");
			System.exit(-1);
		}

		for (int i = 1; i <= MAX_ITERATIONS; i++) {



			Job job = new Job();


			if(i==1) FileInputFormat.addInputPath(job, new Path(args[0])); //inputDirectory
			else FileInputFormat.addInputPath(job,  new Path(args[1]+"/iteration"+String.valueOf(i-1) ) ); //inputDirectory
			//System.out.println("inputPath: "+args[1]+"/iteration"+String.valueOf(i-1));

			FileOutputFormat.setOutputPath(job,new Path(args[1]+"/iteration"+String.valueOf(i) ) ); //outputPath
			System.out.println("outputPath: "+args[1]+"/iteration"+String.valueOf(i));

			job.setJarByClass(PRDriver.class); //jar containing all .classes after compiling .java files
			job.setJobName("PageRankConvergence"+String.valueOf(i)); //do NOT use hythen '-' in name

			// default is TextInputFormat.class: InputFormat<LongWritable,Text>, JobConfigurable
		    //for plain text files. Files r broken into lines. Either linefeed or carriage-return signals end of line. Keys are the position in the file, and values are the line of text.
			job.setInputFormatClass(TextInputFormat.class);
			job.setMapperClass(PRMapper.class);
			job.setMapOutputKeyClass(Text.class);
			job.setMapOutputValueClass(Text.class);

		    //default OutputFormat: a line separated, tab delimited text file of key-value pairs.
			job.setOutputFormatClass(TextOutputFormat.class);
			job.setReducerClass(PRReducer.class);
			job.setOutputKeyClass(Text.class);
			job.setOutputValueClass(Text.class);

			job.waitForCompletion(true);

			Counters ct = job.getCounters();
			float totalResidualChange = ct.findCounter(PRCounters.residualChange).getValue();
			float avgResidualChange = (float)totalResidualChange/(float)DECIMAL_ACCURACY/(float)TOTALNODES;

			System.out.println(" Iteration " + i + " Residual: "
					+ String.format("%.5f", avgResidualChange )+"\n\n\n");

			ct.findCounter(PRCounters.residualChange).setValue(((long)0));
		}



	}

}