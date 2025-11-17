package repositories;

import entities.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@EnableJpaRepositories(basePackages = "repositories")
@EntityScan(basePackages = "entities")
class RepositoriesIntegrationTest {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final MathFunctionRepository mathFunctionRepository;
    private final TabulatedDatasetRepository tabulatedDatasetRepository;
    private final DatasetPointRepository datasetPointRepository;
    private final FunctionComponentRepository functionComponentRepository;
    private final PerformanceMetricRepository performanceMetricRepository;

    RepositoriesIntegrationTest(RoleRepository roleRepository,
                                UserRepository userRepository,
                                UserRoleRepository userRoleRepository,
                                MathFunctionRepository mathFunctionRepository,
                                TabulatedDatasetRepository tabulatedDatasetRepository,
                                DatasetPointRepository datasetPointRepository,
                                FunctionComponentRepository functionComponentRepository,
                                PerformanceMetricRepository performanceMetricRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.mathFunctionRepository = mathFunctionRepository;
        this.tabulatedDatasetRepository = tabulatedDatasetRepository;
        this.datasetPointRepository = datasetPointRepository;
        this.functionComponentRepository = functionComponentRepository;
        this.performanceMetricRepository = performanceMetricRepository;
    }

    @Test
    void shouldPersistSearchAndDeleteUserGraph() {
        Role admin = roleRepository.save(new Role("ADMIN", "Administrators"));
        Role analyst = roleRepository.save(new Role("ANALYST", "Analysts"));

        User user = userRepository.save(new User("user_search", "hashed"));
        userRoleRepository.save(new UserRole(user, admin));
        userRoleRepository.save(new UserRole(user, analyst));

        assertThat(userRepository.existsByUsername("user_search")).isTrue();
        User stored = userRepository.findByUsername("user_search").orElseThrow();
        List<UserRole> roles = userRoleRepository.findByUser(stored);
        assertThat(roles).hasSize(2);

        userRoleRepository.deleteAll(roles);
        userRepository.delete(stored);

        assertThat(userRepository.findByUsername("user_search")).isEmpty();
        assertThat(userRoleRepository.findAll()).isEmpty();
    }

    @Test
    void shouldGenerateQueryAndRemoveDatasetPoints() {
        User owner = userRepository.save(new User("dataset_owner", "pwd"));
        MathFunction function = mathFunctionRepository
                .save(new MathFunction(owner, "Polynomial", MathFunction.FunctionType.ANALYTIC, "{\"body\":\"x^2\"}"));
        TabulatedDataset dataset = tabulatedDatasetRepository
                .save(new TabulatedDataset(function, TabulatedDataset.SourceType.GENERATED));

        datasetPointRepository.save(new DatasetPoint(dataset, 0, new BigDecimal("0.00"), new BigDecimal("0.00")));
        datasetPointRepository.save(new DatasetPoint(dataset, 1, new BigDecimal("1.00"), new BigDecimal("1.00")));
        datasetPointRepository.save(new DatasetPoint(dataset, 2, new BigDecimal("2.00"), new BigDecimal("4.00")));

        assertThat(tabulatedDatasetRepository.findByFunction(function)).hasSize(1);
        assertThat(datasetPointRepository.findByDataset(dataset)).hasSize(3);

        Long datasetId = dataset.getId();
        tabulatedDatasetRepository.delete(dataset);

        assertThat(tabulatedDatasetRepository.findByFunction_Id(function.getId())).isEmpty();
        assertThat(datasetPointRepository.findByDataset_Id(datasetId)).isEmpty();
    }

    @Test
    void shouldHandleFunctionComponentsAndMetrics() {
        User owner = userRepository.save(new User("composer", "pwd"));
        MathFunction composite = mathFunctionRepository.save(
                new MathFunction(owner, "Composite", MathFunction.FunctionType.COMPOSITE, "{\"body\":\"f(g(x))\"}"));
        MathFunction component = mathFunctionRepository.save(
                new MathFunction(owner, "Tabulated", MathFunction.FunctionType.TABULATED, "{\"body\":\"points\"}"));

        FunctionComponent componentLink = functionComponentRepository
                .save(new FunctionComponent(composite, component, (short) 1));

        assertThat(mathFunctionRepository.findByOwner(owner)).hasSize(2);
        assertThat(functionComponentRepository.findByComposite(composite)).containsExactly(componentLink);

        performanceMetricRepository.save(new PerformanceMetric(PerformanceMetric.Engine.MANUAL_JDBC,
                "generation", 100, 25));
        performanceMetricRepository.save(new PerformanceMetric(PerformanceMetric.Engine.FRAMEWORK_ORM,
                "query", 200, 10));

        assertThat(performanceMetricRepository.findByEngine(PerformanceMetric.Engine.MANUAL_JDBC)).hasSize(1);

        functionComponentRepository.delete(componentLink);
        mathFunctionRepository.delete(component);
        mathFunctionRepository.delete(composite);

        assertThat(functionComponentRepository.findByComposite(composite)).isEmpty();
    }
}
