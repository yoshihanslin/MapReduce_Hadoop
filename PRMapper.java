import java.io.IOException;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.conf.Configuration;

import java.util.*;

//in the input file: node/key, previous pageRank, degree, all the destination nodes from souce node

public class PRMapper extends
	Mapper<LongWritable, Text, Text, Text> {
		
		@Override
		public void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException {
				String line = value.toString();
				String lineItems[] = (line.trim()).split("\\s+"); 
				String nodeID = new String(lineItems[0]);
				Float pageRank = Float.parseFloat(lineItems[1]);
				

				context.write(new Text(nodeID), new Text(line.trim()));  //emit old page rank, with degree&destNodeLise if degree>0

				if (lineItems.length>3) {
					Float degree = Float.parseFloat(lineItems[2]); //outlink edges from source to all destination nodes

					String destNodeList[] = lineItems[3].split(",");
					for(int i=0; i<destNodeList.length; i++){
						Float PRperEdge = (float)pageRank/(float)degree;
						context.write(new Text(destNodeList[i]), new Text(PRperEdge.toString()) );
					}

				
				}

			}
		}

		/*
				String year = line.substring(15, 19);
				int airTemperature;
				if (line.charAt(87) == '+') { // parseInt doesn't like leading plus
 				// signs
 				airTemperature = Integer.parseInt(line.substring(88, 92));
 } else {
 airTemperature = Integer.parseInt(line.substring(87, 92));
 }
 String quality = line.substring(92, 93);
 if (airTemperature != MISSING && quality.matches("[01459]")) {
 context.write(new Text(year), new IntWritable(airTemperature));
 }
 }
}
*/