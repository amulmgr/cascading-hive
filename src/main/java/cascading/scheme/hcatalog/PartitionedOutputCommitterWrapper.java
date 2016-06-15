/*
* This file has been created with ideas taken from Twitter's elephant-bird v4.13:
*
* https://github.com/twitter/elephant-bird/tree/elephant-bird-4.13
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package cascading.scheme.hcatalog;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapred.JobContext;
import org.apache.hadoop.mapred.TaskAttemptContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cascading.hadoop.mapred.OutputCommitterWrapper;

public class PartitionedOutputCommitterWrapper extends OutputCommitterWrapper
  {

  private static final Logger LOG = LoggerFactory.getLogger( PartitionedOutputCommitterWrapper.class );

  public static void setOutputCommitter( Configuration conf )
    {
    OutputCommitterWrapper.setOutputCommitter( conf, PartitionedOutputCommitterWrapper.class );
   }

  @Override
  public void setupJob( JobContext jobContext ) throws IOException
    {
    unsetOutputCommitter( jobContext.getConfiguration() );
    super.setupJob( jobContext );
    setOutputCommitter( jobContext.getConfiguration() );
    }

  @Override
  public void commitTask( TaskAttemptContext taskAttemptContext ) throws IOException
    {
    // See https://issues.apache.org/jira/browse/HIVE-10213
    try
      {
      super.commitTask( taskAttemptContext );
      }
    catch ( IOException e )
      {
      if ( e.getMessage().startsWith( "No callback registered for TaskAttemptID:" ) )
        {
        LOG.warn( e.getMessage() + ". Skipping." );
        }
      else
        {
        throw e;
        }
      }
    }

  @Override
  public void abortJob(JobContext jobContext, int status) throws IOException
    {
    unsetOutputCommitter( jobContext.getConfiguration() );
    super.abortJob( jobContext, status );
    setOutputCommitter( jobContext.getConfiguration() );
    }

  @Override
  public void commitJob(JobContext jobContext) throws IOException
    {
    unsetOutputCommitter( jobContext.getConfiguration() );
    super.commitJob( jobContext );
    setOutputCommitter( jobContext.getConfiguration() );
    }

  }