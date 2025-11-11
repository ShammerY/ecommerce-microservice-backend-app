from locust import HttpUser, task, between

class ProductServiceUser(HttpUser):
    # Tiempo de espera entre tareas (para simular comportamiento real)
    wait_time = between(1, 3)

    # Ruta base de tu microservicio
    BASE_PATH = "/product-service/api/products"

    @task(2)
    def get_all_products(self):
        """Obtiene todos los productos"""
        self.client.get(self.BASE_PATH)

    @task(1)
    def get_product_by_id(self):
        """Obtiene un producto específico (usa el ID 1 como ejemplo)"""
        self.client.get(f"{self.BASE_PATH}/1")

    @task(1)
    def create_product(self):
        """Crea un nuevo producto"""
        product = {
            "productTitle": "Locust Laptop",
            "sku": "LOCUST-001",
            "priceUnit": 2500.00,
            "quantity": 10,
            "imageUrl": "https://example.com/laptop.jpg",
            "category": {
                "categoryId": 1
            }
        }
        self.client.post(self.BASE_PATH, json=product)

    @task(1)
    def update_product(self):
        """Actualiza un producto existente"""
        updated_product = {
            "productId": 1,
            "productTitle": "Updated Laptop",
            "sku": "LOCUST-001",
            "priceUnit": 2600.00,
            "quantity": 12,
            "imageUrl": "https://example.com/laptop_updated.jpg",
            "category": {
                "categoryId": 1
            }
        }
        self.client.put(f"{self.BASE_PATH}/1", json=updated_product)

    @task(1)
    def delete_product(self):
        """Intenta eliminar un producto (por ID)"""
        self.client.delete(f"{self.BASE_PATH}/1")
