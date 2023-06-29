
## Locust Setup

```bash
ulimit -S -n 10000

locust -f app.py --only-summary --skip-log-setup  
```