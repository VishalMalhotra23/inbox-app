from faker import Faker
import json
from locust import HttpUser, SequentialTaskSet, task
from locust.exception import StopUser

fake = Faker()
 
class MySQTest(SequentialTaskSet):

    firstuser = fake.email()
    seconduser = fake.email()

    @task(5)
    def send_email_user1(self):
        message = json.dumps({
            "from": self.firstuser,
            "to": self.seconduser,
            "body": fake.sentence(),
            "subject": fake.sentence()
        })

        self.client.post("/api/compose", data=str(message), headers = {'Content-Type': 'application/json'})

    @task(5)
    def send_email_user2(self):
        message = json.dumps({
            "from": self.seconduser,
            "to": self.firstuser,
            "body": fake.sentence(),
            "subject": fake.sentence()
        })

        self.client.post("/api/compose", data=str(message), headers = {'Content-Type': 'application/json'})
        

    @task
    def read_all_email_user1(self):
        res = self.client.get(f"/api/email/{self.firstuser}/i")
        data = json.loads(res.text)
        
        for i in range(len(data)):
            x = data[i].get("id").get("timeId") 
            self.client.get(f"/api/email/{x}")

    @task
    def read_all_email_user2(self):
        res = self.client.get(f"/api/email/{self.seconduser}/s")
        data = json.loads(res.text)
        
        for i in range(len(data)):
            x = data[i].get("id").get("timeId") 
            self.client.get(f"/api/email/{x}")
    
class MyLoad(HttpUser):

    host = "http://localhost:8080"
    tasks = [MySQTest]