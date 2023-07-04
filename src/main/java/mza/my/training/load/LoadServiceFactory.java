package mza.my.training.load;

import org.springframework.lang.Nullable;

public interface LoadServiceFactory {
    //LoadResultDto load(@Nullable TrainingFilters trainingFilters);
    LoadResultDto load(@Nullable TrainingFilters trainingFilters, @Nullable String sortingField, boolean ascending);
}
