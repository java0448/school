package ru.t1.school.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;
import ru.t1.school.dto.TaskDTO;
import ru.t1.school.mapper.TaskMapper;
import ru.t1.school.repository.TaskRepository;
import ru.t1.school.entity.Task;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@DirtiesContext
public class TaskServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskRepository taskRepository;

    private final TaskMapper taskMapper = TaskMapper.INSTANCE;

    private static final int KAFKA_PORT = 9092;

    @Container
    public static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:13.3");

    @Container
    public static KafkaContainer kafkaContainer = new KafkaContainer(
            DockerImageName.parse("apache/kafka:latest"))
            .withExposedPorts(KAFKA_PORT);

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
    }

    @BeforeEach
    public void setUp() {
        taskRepository.deleteAll();
    }

    @Test
    @DisplayName("Создание новой задачи")
    public void testCreateTask() throws Exception {
        TaskDTO taskDTO = new TaskDTO(null, "Test Task", "Test Description", 1L, "NEW");

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is("Test Task")))
                .andExpect(jsonPath("$.description", is("Test Description")))
                .andExpect(jsonPath("$.userId", is(1)))
                .andExpect(jsonPath("$.status", is("NEW")));
    }

    @Test
    @DisplayName("Получение задачи по ID")
    public void testGetTaskById() throws Exception {
        TaskDTO taskDTO = new TaskDTO(null, "Test Task", "Test Description", 1L, "NEW");
        Task savedTask = taskRepository.save(taskMapper.toEntity(taskDTO));

        mockMvc.perform(get("/tasks/{id}", savedTask.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Test Task")))
                .andExpect(jsonPath("$.description", is("Test Description")))
                .andExpect(jsonPath("$.userId", is(1)))
                .andExpect(jsonPath("$.status", is("NEW")));
    }

    @Test
    @DisplayName("Получение задачи по ID - задача не найдена")
    public void testGetTaskByIdNotFound() throws Exception {
        mockMvc.perform(get("/tasks/{id}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Task not found with id 1"));
    }

    @Test
    @DisplayName("Обновление задачи")
    public void testUpdateTask() throws Exception {
        TaskDTO taskDTO = new TaskDTO(null, "Test Task", "Test Description", 1L, "NEW");
        Task savedTask = taskRepository.save(taskMapper.toEntity(taskDTO));

        TaskDTO updatedTaskDTO = new TaskDTO(savedTask.getId(), "Updated Task", "Updated Description", 1L, "UPDATE");

        mockMvc.perform(put("/tasks/{id}", savedTask.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedTaskDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Updated Task")))
                .andExpect(jsonPath("$.description", is("Updated Description")))
                .andExpect(jsonPath("$.userId", is(1)))
                .andExpect(jsonPath("$.status", is("UPDATE")));

        // Дополнительная проверка данных в базе данных
        Task updatedTask = taskRepository.findById(savedTask.getId()).orElseThrow();
        assertEquals("Updated Task", updatedTask.getTitle());
        assertEquals("Updated Description", updatedTask.getDescription());
        assertEquals(1L, updatedTask.getUserId());
        assertEquals("UPDATE", updatedTask.getStatus());
    }

    @Test
    @DisplayName("Обновление задачи - задача не найдена")
    public void testUpdateTaskNotFound() throws Exception {
        TaskDTO updatedTaskDTO = new TaskDTO(1L, "Updated Task", "Updated Description", 1L, "UPDATE");

        mockMvc.perform(put("/tasks/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedTaskDTO)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Task not found with id 1"));
    }

    @Test
    @DisplayName("Удаление задачи")
    public void testDeleteTask() throws Exception {
        TaskDTO taskDTO = new TaskDTO(null, "Test Task", "Test Description", 1L, "NEW");
        Task savedTask = taskRepository.save(taskMapper.toEntity(taskDTO));

        mockMvc.perform(delete("/tasks/{id}", savedTask.getId()))
                .andExpect(status().isNoContent());

        assertFalse(taskRepository.existsById(savedTask.getId()));
    }

    @Test
    @DisplayName("Удаление задачи - задача не найдена")
    public void testDeleteTaskNotFound() throws Exception {
        mockMvc.perform(delete("/tasks/{id}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Task not found with id 1"));
    }

    @Test
    @DisplayName("Получение всех задач")
    public void testGetAllTasks() throws Exception {
        TaskDTO taskDTO1 = new TaskDTO(null, "Test Task 1", "Test Description 1", 1L, "NEW");
        TaskDTO taskDTO2 = new TaskDTO(null, "Test Task 2", "Test Description 2", 2L, "UPDATE");
        taskRepository.save(taskMapper.toEntity(taskDTO1));
        taskRepository.save(taskMapper.toEntity(taskDTO2));

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(2)))
                .andExpect(jsonPath("$[0].title", is("Test Task 1")))
                .andExpect(jsonPath("$[1].title", is("Test Task 2")));
    }
}
