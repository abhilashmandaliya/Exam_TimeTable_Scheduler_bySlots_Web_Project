package edu.daiict;

import edu.daiict.config.ExamTimeTableSchedulerConfig;
import edu.daiict.resource.BatchProgramResource;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;

public class ExamTimeTableSchedulerApplication extends Application<ExamTimeTableSchedulerConfig> {

    @Override
    public void initialize(Bootstrap<ExamTimeTableSchedulerConfig> bootstrap) {
        super.initialize(bootstrap);
        bootstrap.addBundle(new ViewBundle<>());
        bootstrap.addBundle(new AssetsBundle("/assets", "/static"));
    }

    @Override
    public void run(ExamTimeTableSchedulerConfig examTimeTableSchedulerConfig, Environment environment) {
        BatchProgramResource batchProgramResource = new BatchProgramResource();

        environment.jersey().register(batchProgramResource);
    }

    public static void main(String[] args) throws Exception {
        new ExamTimeTableSchedulerApplication().run(args);
    }
}
