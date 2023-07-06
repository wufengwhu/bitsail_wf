package com.bytedance.bitsail.connector.legacy.streamingfile.sink;

import com.bytedance.bitsail.base.execution.ExecutionEnviron;
import com.bytedance.bitsail.common.configuration.BitSailConfiguration;
import com.bytedance.bitsail.connector.legacy.streamingfile.common.filesystem.OutputFormatFactory;
import com.bytedance.bitsail.connector.legacy.streamingfile.common.filesystem.PartitionComputer;
import com.bytedance.bitsail.connector.legacy.streamingfile.common.filesystem.PartitionWriterFactory;
import com.bytedance.bitsail.connector.legacy.streamingfile.core.sink.FileSystemCommitter;
import com.bytedance.bitsail.connector.legacy.streamingfile.core.sink.StreamingFileSystemSink;
import com.bytedance.bitsail.connector.legacy.streamingfile.core.sink.format.AbstractFileSystemFactory;
import com.bytedance.bitsail.flink.core.writer.FlinkDataWriterDAGBuilder;
import org.apache.flink.streaming.api.functions.sink.filesystem.StreamingFileSink;
import org.apache.flink.table.descriptors.DescriptorProperties;
import org.apache.flink.types.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StreamingFileSinkFunctionDAGBuilder <OUT extends Row> extends FlinkDataWriterDAGBuilder<OUT> {

    private static final Logger LOG = LoggerFactory.getLogger(StreamingFileSinkFunctionDAGBuilder.class);

    protected BitSailConfiguration jobConf;
    protected DescriptorProperties descriptorProperties;

    private StreamingFileSink<OUT> fileSystemSink;

    @Override
    public void configure(ExecutionEnviron execution, BitSailConfiguration writerConfiguration) throws Exception {
        jobConf = execution.getGlobalConfiguration();
        descriptorProperties = new DescriptorProperties(true);
        this.fileSystemSink = getStreamingFileSystemSink(jobConf);
        this.sinkFunction = fileSystemSink;
    }

    private StreamingFileSink<OUT> getStreamingFileSystemSink(final BitSailConfiguration jobConf) throws Exception {
//        AbstractFileSystemFactory<OUT> fileSystemFactory = getFileSystemFactory(jobConf);
//        OutputFormatFactory<OUT> outputFormatFactory = fileSystemFactory.createOutputFormatFactory();
//        PartitionComputer<OUT> partitionComputer = fileSystemFactory.createPartitionComputer();
//        PartitionWriterFactory<OUT> partitionWriterFactory = fileSystemFactory.createPartitionWriterFactory();
//        FileSystemCommitter committer = fileSystemFactory.createFileSystemCommitter();
//        committer.overwriteDirtyCollector();
//
//        return new StreamingFileSystemSink<>(
//                outputFormatFactory,
//                partitionComputer,
//                partitionWriterFactory,
//                committer,
//                jobConf);
        return null;
    }

    @Override
    public boolean validate() throws Exception {
        return super.validate();
    }

    @Override
    public String getWriterName() {
        return null;
    }
}
