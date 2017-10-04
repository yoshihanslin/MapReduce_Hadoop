import java.io.IOException;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import java.util.*;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.Counters;
import org.apache.hadoop.mapreduce.Counter;

//in the input file: node/key, previous pageRank, degree, all the destination nodes from souce node

public class PRReducer
	extends Reducer<Text, Text, Text, Text> {
		private static final float d = 0.85f; //damping factor
	@Override
	public void reduce(Text key, Iterable<Text> values, Context context)
 		throws IOException, InterruptedException {
 			String outDegreeEdgeList="";
 			Text nd = new Text();
 			nd = key;
 			String nodeID = nd.toString();
 			Iterator<Text> itr = values.iterator();
 			float sumInEdgePR = 0.0f;
 			float oldPageRank=0.0f;
			while(itr.hasNext()){
				String val= ((itr.next()).toString()).trim(); //Text to String
				//System.out.println("val "+ val);
				String lineItems[] = (val.trim()).split("\\s+"); 
				// value is source node/key, previous pageRank, degree, all the destination nodes from souce node
				//          lineItems[0]   , lineItems[1],    , lineItems[2], lineItems[3]
				if (lineItems.length==4 && lineItems[0].equals(nodeID)){
					oldPageRank = new Float(lineItems[1]);
					outDegreeEdgeList = lineItems[2]+" "+lineItems[3];
				}
				else if ((lineItems.length==2 || lineItems.length==3) && lineItems[0].equals(nodeID)) {
					oldPageRank = new Float(lineItems[1]);
				}
				else { // lineItems.length==1,  value is a incoming page rank edge
						sumInEdgePR += new Float(val);
		 		}
	 		}

			//System.out.println("list "+ outDegreeEdgeList);

 			float newPR = (1.0f-d)/(float)PRDriver.TOTALNODES + d*sumInEdgePR;

			float residualChange = Math.abs((newPR - oldPageRank)*PRDriver.DECIMAL_ACCURACY) / (float)newPR;

			Counter residualCnt = context.getCounter(PRDriver.PRCounters.residualChange);
			residualCnt.increment((long)residualChange);

 			//default TextOutputFormat: a line separated, tab delimited text file of key-value pairs.
 			context.write(new Text(nodeID), new Text(String.valueOf(newPR)+" "+outDegreeEdgeList)); //will be written to outputfile
 		}
 }

/*
public class MaxTemperatureReducer
extends Reducer<Text, IntWritable, Text, IntWritable> {
@Override
public void reduce(Text key, Iterable<IntWritable> values,
 Context context)
 throws IOException, InterruptedException {

 int maxValue = Integer.MIN_VALUE;
 for (IntWritable value : values) {
 maxValue = Math.max(maxValue, value.get());
 }
 context.write(key, new IntWritable(maxValue));
}
}
*/