# ☕ BeanHaven Cloud-Native DevOps Project

BeanHaven is a cloud-native coffee shop web application built with **Spring Boot** and **MySQL**, containerized using **Docker**, and deployed using **Kubernetes**.

This project demonstrates a complete DevOps workflow including CI/CD automation, containerization, Kubernetes deployment, autoscaling, persistent database storage, health checks, rolling updates, and container image publishing.

---

## 🚀 Technologies Used

- Java 21
- Spring Boot
- Maven
- MySQL
- Docker
- Kubernetes
- k3d / K3s
- GitHub
- GitHub Actions
- GitHub Container Registry (GHCR)
- Traefik Ingress
- Horizontal Pod Autoscaler (HPA)

---



## ⚙️ DevOps Features

- Automated Maven build using GitHub Actions
- Automated application testing
- Dockerized Spring Boot backend
- Docker image publishing to GHCR
- Kubernetes Deployment
- Multiple backend replicas
- Kubernetes Services
- Traefik Ingress routing
- Readiness probes
- Liveness probes
- CPU and memory resource requests and limits
- Horizontal Pod Autoscaler (HPA)
- MySQL deployment in Kubernetes
- Persistent storage using PVC
- Kubernetes ConfigMap
- Kubernetes Secrets
- Rolling updates
- Kubernetes self-healing

---

## 📁 Project Structure

```text
beanhaven-cloud-devops/
│
├── .github/
│   └── workflows/
│       └── ci-cd.yml
│
├── kubernetes/
│   ├── backend-deployment.yaml
│   ├── backend-hpa.yaml
│   ├── configmap.yaml
│   ├── ingress.yaml
│   ├── mysql-deployment.yaml
│   └── mysql-pvc.yaml
│
├── src/
│   └── main/
│       ├── java/
│       └── resources/
│
├── Dockerfile
├── pom.xml
└── README.md
```

> `kubernetes/secret.yaml` is intentionally excluded from Git to prevent sensitive credentials from being committed to the repository.

---

## 🔄 CI/CD Pipeline

The project uses **GitHub Actions** for Continuous Integration and container image publishing.

When code is pushed to the `main` branch:

1. GitHub Actions checks out the source code.
2. Java 21 is configured.
3. Maven builds the Spring Boot application.
4. Application tests are executed.
5. A Docker image is built.
6. The Docker image is published to GitHub Container Registry (GHCR).
7. The Kubernetes deployment can use the published GHCR image.

This provides an automated workflow from source code to a deployable container image.

---

## 🐳 Docker

The Spring Boot backend is packaged into a Docker image using the project `Dockerfile`.

The container exposes the backend application on:

```text
8081
```

The CI/CD pipeline builds and publishes the application image to **GitHub Container Registry**.

---

## ☸️ Kubernetes Deployment

The application is deployed to a Kubernetes cluster running through **k3d/K3s**.

Check the cluster nodes:

```bash
kubectl get nodes
```

Check running pods:

```bash
kubectl get pods
```

Check the backend deployment:

```bash
kubectl get deployment ecommerce-backend
```

Check rollout status:

```bash
kubectl rollout status deployment/ecommerce-backend
```

A successful rolling deployment displays:

```text
deployment "ecommerce-backend" successfully rolled out
```

---

## 🌐 Kubernetes Ingress

**Traefik Ingress** is used to route requests to the backend service.

For local testing, Traefik can be forwarded to port `8084`:

```bash
kubectl -n kube-system port-forward service/traefik 8084:80
```

The application API can then be accessed through:

```text
http://ecommerce.local:8084
```

---

## 🧪 API Testing

The Coffee API can be tested using PowerShell:

```powershell
Invoke-WebRequest -UseBasicParsing http://ecommerce.local:8084/api/coffees
```

A successful deployment returns:

```text
StatusCode        : 200
StatusDescription : OK
```

This confirms that traffic is successfully routed through Kubernetes to the Spring Boot application.

---

## 📈 Horizontal Pod Autoscaling

The backend uses a **Horizontal Pod Autoscaler (HPA)**.

The HPA can automatically increase or decrease the number of backend pods depending on CPU utilization.

Check the HPA using:

```bash
kubectl get hpa
```

The deployment is configured with:

```text
Minimum replicas: 2
Maximum replicas: 5
Target CPU utilization: 50%
```

---

## ❤️ Readiness and Liveness Probes

Kubernetes health checks are configured for the backend containers.

### Readiness Probe

Determines whether a backend pod is ready to receive traffic.

### Liveness Probe

Determines whether the application is healthy. Kubernetes can restart unhealthy containers when required.

These probes improve application reliability and availability.

---

## 💾 MySQL Persistent Storage

MySQL runs inside the Kubernetes cluster.

A **Persistent Volume Claim (PVC)** is used so database data can persist independently of the MySQL pod lifecycle.

Check the PVC using:

```bash
kubectl get pvc
```

---

## 🔐 Security

Sensitive configuration is separated from application source code.

The project uses:

- Kubernetes Secrets for sensitive values
- Kubernetes ConfigMap for non-sensitive configuration
- Environment variables for application configuration
- BCrypt for administrator password storage

The following file is excluded from Git:

```text
kubernetes/secret.yaml
```

Passwords and other sensitive credentials should never be committed directly to the public repository.

---

## 🔄 Rolling Updates

Kubernetes rolling updates allow a new application version to be deployed while existing replicas continue serving requests.

Deployment status can be monitored using:

```bash
kubectl rollout status deployment/ecommerce-backend
```

This project successfully verified a Kubernetes rolling update with the new container image.

---

## 🛠️ Kubernetes Self-Healing

Kubernetes automatically maintains the desired number of application replicas.

If a backend pod fails or is deleted, Kubernetes automatically creates a replacement pod.

This provides improved availability and fault tolerance.

---

## 📊 Useful Kubernetes Commands

```bash
kubectl get nodes
kubectl get pods
kubectl get deployments
kubectl get services
kubectl get ingress
kubectl get hpa
kubectl get pvc
```

View backend logs:

```bash
kubectl logs <backend-pod-name>
```

Monitor a rollout:

```bash
kubectl rollout status deployment/ecommerce-backend
```

---

## ✅ Project Result

The BeanHaven application was successfully:

- Built using Spring Boot and Maven
- Connected to MySQL
- Containerized using Docker
- Published to GitHub Container Registry
- Deployed to Kubernetes
- Scaled using multiple replicas
- Configured with HPA
- Protected using Kubernetes Secrets
- Configured with persistent MySQL storage
- Exposed through Traefik Ingress
- Tested successfully through the Kubernetes environment
- Integrated with GitHub Actions CI/CD

---

## 👩‍💻 Author

**Jayamini Dissanayake**

BSc (Hons) Computer Systems Engineering Undergraduate  
Sri Lanka Institute of Information Technology (SLIIT)

**GitHub:** dissaanayake-23