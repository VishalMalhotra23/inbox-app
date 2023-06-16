from faker import Faker
import json
from locust import FastHttpUser, SequentialTaskSet, task
from gevent.pool import Pool
import time

fake = Faker()

class MySQTest(SequentialTaskSet):

    @task
    def generateLoad(self):

        firstuser = fake.email()
        seconduser = fake.email()

        for i in range(5):

            message = json.dumps({
                "from": firstuser,
                "to": seconduser,
                "body": fake.sentence(),
                "subject": fake.sentence()
            })

            self.client.post("/api/compose", data=str(message), headers = {'Content-Type': 'application/json'})

            message = json.dumps({
                "from": seconduser,
                "to": firstuser,
                "body": fake.sentence(),
                "subject": fake.sentence()
            })

            self.client.post("/api/compose", data=str(message), headers = {'Content-Type': 'application/json'})

        time.sleep(1)
               
        res = self.client.get(f"/api/email/{firstuser}/i")
        data = json.loads(res.text)

        time.sleep(1)

        def concurrent_request1(i):
            x = data[i].get("id").get("timeId") 
            self.client.get(f"/api/email/{x}")

        pool = Pool()
        for i in range(len(data)):
            pool.spawn(concurrent_request1,i)
        pool.join()
        
        res = self.client.get(f"/api/email/{seconduser}/i")
        data = json.loads(res.text)

        time.sleep(1)
        def concurrent_request(i):
            x = data[i].get("id").get("timeId") 
            self.client.get(f"/api/email/{x}")

        pool = Pool()
        for i in range(len(data)):
            pool.spawn(concurrent_request,i)
        pool.join()

class MyLoad(FastHttpUser):

    host = "http://10.128.0.24"
    tasks = [MySQTest]