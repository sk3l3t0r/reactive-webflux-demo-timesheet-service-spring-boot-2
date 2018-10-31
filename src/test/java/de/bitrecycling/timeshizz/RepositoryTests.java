package de.bitrecycling.timeshizz;

import de.bitrecycling.timeshizz.model.Client;
import de.bitrecycling.timeshizz.model.Project;
import de.bitrecycling.timeshizz.model.Task;
import de.bitrecycling.timeshizz.model.TaskEntry;
import de.bitrecycling.timeshizz.repository.ClientRepository;
import de.bitrecycling.timeshizz.repository.ProjectRespository;
import de.bitrecycling.timeshizz.repository.TaskEntryRepository;
import de.bitrecycling.timeshizz.repository.TaskRepository;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.time.LocalDateTime;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TimeshizzApplication.class)
@AutoConfigureMockMvc
public class RepositoryTests {

    @Autowired
    ClientRepository clientRepository;
    @Autowired
    ProjectRespository projectRespository;
    @Autowired
    TaskRepository taskRepository;
    @Autowired
    TaskEntryRepository taskEntryRepository;

    @After
    public void teardown() {
        clientRepository.deleteAll().subscribe();
        projectRespository.deleteAll().subscribe();
        taskRepository.deleteAll().subscribe();
        taskEntryRepository.deleteAll().subscribe();
    }



    @Test
    public void fullTurnaround() {
        Client c = new Client();
        c.setName("fullTestClient");
        c.setAddress("fullTestClientAddress");
        clientRepository.insert(c).subscribe();
        Project p = new Project();
        p.setDescription("fullTestProjectDescription");
        p.setName("fullTestProjectName");
        p.setRate(100);
        projectRespository.insert(p).subscribe();
        Task t = new Task();
        t.setName("fullTestTaskName");
        t.setCreationTime(LocalDateTime.now());
        taskRepository.insert(t).subscribe();
        TaskEntry te1 = new TaskEntry(LocalDateTime.now(), LocalDateTime.now().plusHours(5));
        TaskEntry te2 = new TaskEntry(Duration.between(LocalDateTime.now(), LocalDateTime.now().plusHours(3)));
        t.getTaskEntryIds().add(te1.getId());
        t.getTaskEntryIds().add(te2.getId());
        taskEntryRepository.insert(te1).subscribe();
        taskEntryRepository.insert(te2).subscribe();
        taskRepository.save(t).subscribe();
        p.getTaskIds().add(t.getId());
        projectRespository.save(p).subscribe();
        c.getProjectIds().add(p.getId());
        clientRepository.save(c).subscribe();

        StepVerifier.create(
                clientRepository.findAll()).expectNextMatches(x-> c.equals(x)).verifyComplete();

    }

    @Test
    public void testProjectsByClient(){
        Client c = createTestData();
        StepVerifier.create(projectRespository.findAllByClient(Mono.just(c)))
                .expectNextMatches(
                        project -> project.getName().equals( "fullTestProjectName"))
                .verifyComplete();
    }

    private Client createTestData() {
        Client c = new Client();
        c.setName("fullTestClient");
        c.setAddress("fullTestClientAddress");
        clientRepository.insert(c).subscribe();
        Project p = new Project();
        p.setDescription("fullTestProjectDescription");
        p.setName("fullTestProjectName");
        p.setRate(100);
        projectRespository.insert(p).subscribe();
        Task t = new Task();
        t.setName("fullTestTaskName");
        t.setCreationTime(LocalDateTime.now());
        taskRepository.insert(t).subscribe();
        TaskEntry te1 = new TaskEntry(LocalDateTime.now(), LocalDateTime.now().plusHours(5));
        TaskEntry te2 = new TaskEntry(Duration.between(LocalDateTime.now(), LocalDateTime.now().plusHours(3)));
        t.getTaskEntryIds().add(te1.getId());
        t.getTaskEntryIds().add(te2.getId());
        taskEntryRepository.insert(te1).subscribe();
        taskEntryRepository.insert(te2).subscribe();
        taskRepository.save(t).subscribe();
        p.getTaskIds().add(t.getId());
        projectRespository.save(p).subscribe();
        c.getProjectIds().add(p.getId());
        clientRepository.save(c).subscribe();
        return c;
    }
}
