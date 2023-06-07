from faker import Faker
import json
from locust import FastHttpUser, SequentialTaskSet, task
from gevent.pool import Pool
import time

fake = Faker()
 
class MySQTest(SequentialTaskSet):

    firstuser = fake.email()
    seconduser = fake.email()

    @task
    def send_email_user(self):

        def concurrent_request():
            message = json.dumps({
                "from": self.firstuser,
                "to": self.seconduser,
                "body": fake.sentence(),
                "subject": fake.sentence()
            })

            self.client.post("/api/compose", data=str(message), headers = {'Content-Type': 'application/json'})
            
            message = json.dumps({
                "from": self.seconduser,
                "to": self.firstuser,
                "body": fake.sentence(),
                "subject": fake.sentence()
            })

            self.client.post("/api/compose", data=str(message), headers = {'Content-Type': 'application/json'})
        
        pool = Pool()
        for i in range(5):
            pool.spawn(concurrent_request)
        pool.join()



    # @task
    # def send_email_user2(self):

    #     def concurrent_request():
    #         message = json.dumps({
    #             "from": self.seconduser,
    #             "to": self.firstuser,
    #             "body": fake.sentence(),
    #             "subject": fake.sentence()
    #         })

    #         self.client.post("/api/compose", data=str(message), headers = {'Content-Type': 'application/json'})
        
    #     pool = Pool()
    #     for i in range(5):
    #         pool.spawn(concurrent_request)
    #     pool.join()
        

    @task
    def read_all_email_user1(self):
        time.sleep(1) 

        res = self.client.get(f"/api/email/{self.firstuser}/i")
        data = json.loads(res.text)

        def concurrent_request(i):        
            x = data[i].get("id").get("timeId") 
            self.client.get(f"/api/email/{x}")
        
        pool = Pool()
        for i in range(len(data)):
            pool.spawn(concurrent_request, i)
        pool.join()

    @task
    def read_all_email_user2(self):
        time.sleep(1) 
        res = self.client.get(f"/api/email/{self.seconduser}/i")
        data = json.loads(res.text)

        def concurrent_request(i):        
            x = data[i].get("id").get("timeId") 
            self.client.get(f"/api/email/{x}")
        
        pool = Pool()
        for i in range(len(data)):
            pool.spawn(concurrent_request, i)
        pool.join()
    
class MyLoad(FastHttpUser):

    host = "http://localhost:8080"
    tasks = [MySQTest]