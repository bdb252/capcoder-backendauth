package com.ccdweb.springboot.nutrition.loader;

import com.ccdweb.springboot.nutrition.entity.FoodEntity;
import com.ccdweb.springboot.nutrition.repository.FoodRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class FoodDataLoader {

    private final FoodRepository foodRepository;

    @Value("${app.data.preload:true}")
    private boolean enableDataPreload;

    @PostConstruct
    public void loadData() {
        if (!enableDataPreload) {
            log.info("Data preload disabled");
            return;
        }

        if (foodRepository.count() > 0) {
            log.info("Food data already exists, skipping preload");
            return;
        }

        try {
            log.info("Loading food data from CSV...");

            ClassPathResource resource = new ClassPathResource("data/processed/processed_food.csv");

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {

                CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                        .withFirstRecordAsHeader()
                        .withIgnoreHeaderCase()
                        .withTrim());

                List<FoodEntity> foods = new ArrayList<>();
                int batchSize = 500;
                int totalCount = 0;

                for (CSVRecord record : csvParser) {
                    FoodEntity food = new FoodEntity();

                    food.setFoodCode(record.get("식품코드"));
                    food.setFoodName(record.get("식품명"));
                    food.setEnergy(parseDouble(record.get("에너지(kcal)")));
                    food.setCategoryLarge(record.get("식품대분류명"));
                    food.setCategoryMedium(record.get("식품중분류명"));
                    food.setCategorySmall(record.get("식품소분류명"));
                    food.setServingSizeStandard(record.get("영양성분함량기준량"));
                    food.setFoodWeight(parseDouble(record.get("식품중량")));

                    food.setProtein(parseDouble(record.get("단백질(g)")));
                    food.setSugars(parseDouble(record.get("당류(g)")));
                    food.setCarbohydrates(parseDouble(record.get("탄수화물(g)")));
                    food.setFat(parseDouble(record.get("지방(g)")));

                    food.setDietaryFiber(parseDouble(record.get("식이섬유(g)")));
                    food.setDietaryFiberIsMissing(parseInteger(record.get("식이섬유(g)_is_missing")));

                    food.setWater(parseDouble(record.get("수분(g)")));
                    food.setWaterIsMissing(parseInteger(record.get("수분(g)_is_missing")));

                    food.setSodium(parseDouble(record.get("나트륨(mg)")));
                    food.setSaturatedFat(parseDouble(record.get("포화지방산(g)")));

                    food.setPotassium(parseDouble(record.get("칼륨(mg)")));
                    food.setPotassiumIsMissing(parseInteger(record.get("칼륨(mg)_is_missing")));

                    food.setVitaminD(parseDouble(record.get("비타민 D(μg)")));
                    food.setVitaminDIsMissing(parseInteger(record.get("비타민 D(μg)_is_missing")));

                    food.setIron(parseDouble(record.get("철(mg)")));
                    food.setIronIsMissing(parseInteger(record.get("철(mg)_is_missing")));

                    food.setThiamin(parseDouble(record.get("티아민(mg)")));
                    food.setThiaminIsMissing(parseInteger(record.get("티아민(mg)_is_missing")));

                    food.setNiacin(parseDouble(record.get("니아신(mg)")));
                    food.setNiacinIsMissing(parseInteger(record.get("니아신(mg)_is_missing")));

                    food.setVitaminA(parseDouble(record.get("비타민 A(μg RAE)")));
                    food.setVitaminAIsMissing(parseInteger(record.get("비타민 A(μg RAE)_is_missing")));

                    food.setVitaminC(parseDouble(record.get("비타민 C(mg)")));
                    food.setVitaminCIsMissing(parseInteger(record.get("비타민 C(mg)_is_missing")));

                    foods.add(food);

                    if (foods.size() >= batchSize) {
                        foodRepository.saveAll(foods);
                        totalCount += foods.size();
                        log.info("Progress: {}/~14584 loaded ({} %)", totalCount, (totalCount * 100) / 14584);
                        foods.clear();
                    }
                }

                if (!foods.isEmpty()) {
                    foodRepository.saveAll(foods);
                    totalCount += foods.size();
                }

                log.info("Successfully loaded {} food items", totalCount);
            }

        } catch (Exception e) {
            log.error("Failed to load food data", e);
        }
    }

    private Double parseDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (Exception e) {
            return null;
        }
    }

    private Integer parseInteger(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return null;
        }
    }
}
