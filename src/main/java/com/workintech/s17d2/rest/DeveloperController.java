package com.workintech.s17d2.rest;

import com.workintech.s17d2.model.Developer;
import com.workintech.s17d2.model.Experience;
import com.workintech.s17d2.model.JuniorDeveloper;
import com.workintech.s17d2.model.MidDeveloper;
import com.workintech.s17d2.model.SeniorDeveloper;
import com.workintech.s17d2.tax.Taxable;
import jakarta.annotation.PostConstruct;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/developers")
public class DeveloperController {

    public Map<Integer, Developer> developers;

    private final Taxable taxable;

    public DeveloperController(Taxable taxable) {
        this.taxable = taxable;
    }

    @PostConstruct
    public void init() {
        developers = new HashMap<>();
    }

    @GetMapping
    public List<Developer> getDevelopers() {
        return new ArrayList<>(developers.values());
    }

    @GetMapping("/{id}")
    public Developer getDeveloperById(@PathVariable int id) {
        return developers.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Developer addDeveloper(@RequestBody Developer developer) {

        Developer newDeveloper = createDeveloper(
                developer.getId(),
                developer.getName(),
                developer.getSalary(),
                developer.getExperience()
        );

        developers.put(newDeveloper.getId(), newDeveloper);

        return newDeveloper;
    }

    @PutMapping("/{id}")
    public Developer updateDeveloper(
            @PathVariable int id,
            @RequestBody Developer developer) {

        Developer updatedDeveloper = createDeveloper(
                id,
                developer.getName(),
                developer.getSalary(),
                developer.getExperience()
        );

        developers.put(id, updatedDeveloper);

        return updatedDeveloper;
    }

    @DeleteMapping("/{id}")
    public Developer deleteDeveloper(@PathVariable int id) {
        return developers.remove(id);
    }

    private Developer createDeveloper(
            int id,
            String name,
            double salary,
            Experience experience) {

        if (experience == Experience.JUNIOR) {
            double netSalary =
                    salary - (salary * taxable.getSimpleTaxRate() / 100);

            return new JuniorDeveloper(id, name, netSalary);
        }

        if (experience == Experience.MID) {
            double netSalary =
                    salary - (salary * taxable.getMiddleTaxRate() / 100);

            return new MidDeveloper(id, name, netSalary);
        }

        double netSalary =
                salary - (salary * taxable.getUpperTaxRate() / 100);

        return new SeniorDeveloper(id, name, netSalary);
    }
}
