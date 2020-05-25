package edu.daiict.view;

import edu.daiict.model.BatchProgram;
import io.dropwizard.views.View;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Collection;

@Getter
@EqualsAndHashCode(callSuper = false)
public class BatchProgramView extends View {

    private final Collection<BatchProgram> batchPrograms;

    public BatchProgramView(Collection<BatchProgram> batchPrograms) {
        super("batch-program.ftl");
        this.batchPrograms = batchPrograms;
    }
}
