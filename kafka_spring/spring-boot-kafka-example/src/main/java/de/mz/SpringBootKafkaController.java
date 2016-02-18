package de.mz;

import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SpringBootKafkaController {

	@Autowired
	SpringBootKafkaProducer springBootKafkaProducer;

	@RequestMapping("/vote")
	public Status vote(@RequestBody Vote vote) throws ExecutionException, InterruptedException {

		springBootKafkaProducer.send(vote.getName());

		return new Status("ok");
	}

}