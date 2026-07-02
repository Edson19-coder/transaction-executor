# Transaction Executor
Servicio encargado de validar, procesar y registrar transacciones.

## Stack Técnico
* **Lenguaje:** Java
* **Framework:** Spring Boot
* **Base de Datos:**  Oracle
* **Mensajeria:** Json

## Requisitos Previos
* [Amazon Corretto 21](https://docs.aws.amazon.com/corretto/latest/corretto-21-ug/downloads-list.html)
* [Gradle 8.5](https://gradle.org/releases/)
* Docker & Docker Compose (Instalados y en ejecución).
* Cliente de Base de Datos para la configuración inicial de Oracle.

## Configuración Inicial
1. **Clonar el repositorio:**
```bash
   git clone https://gitlab.com/netpaymx/terminalizacion/smart-reports
```
2. **Archivo de configuración para despliegue local:** Se debe de generar un archivo local de configuración en la carpeta resource del proyecto, esto base al archivo de despliegue de desarrollo: [smart-reports-dev.yml](https://gitlab.com/netpaymx/configuracion-cloud/ejemplo-produccion/-/blob/master/smart-reports-dev.yml)
3. **Build:** Generar jar de la aplicación previamente con gradle:
```bash
    gradle build
```
4. **Iniciar proyecto:** Ubicarse en la carpeta del jar generado y ejecutar jar con el perfil deseado:
```bash
    java -jar -Dspring.profiles.active=dev smart-reports-0.0.1-SNAPSHOT.jar
```
### Configuración Previa de la Base de Datos
La aplicación requiere un usuario exclusivo con permisos para ejecutar los scripts y paquetes almacenados. Sigue estos pasos en tu instancia de Oracle:
1. **Conéctate como administrador (SYS AS SYSDBA).**
2. **Ejecuta el siguiente script para crear el usuario y otorgar los permisos necesarios:**
```bash
   -- 1. Crear el usuario/esquema
    CREATE USER USER_APP IDENTIFIED BY "Password123";
    
    -- 2. Otorgar permisos básicos de conexión y desarrollo
    GRANT CONNECT, RESOURCE TO USER_APP;
    GRANT CREATE VIEW, CREATE SEQUENCE TO USER_APP;
    
    -- 3. Asignar cuota de espacio en el tablespace
    ALTER USER USER_APP QUOTA UNLIMITED ON USERS;
```
## CI/CD y Despliegue
* Entorno de desarrollo: Se despliega automáticamente al hacer merge a develop. Los logs se pueden ver en la consola de AWS > CloudWatch > smart-reports
* Entorno de producción: Requiere aprobación manual por los responsables de instalación así como previamente debe de haber aprobación de un miembro del grupo: **ntpy-tech-leaders**

## Contribución
1. Crea una rama con el formato correspondiente especificado en la documentación de [estándares de nomenclatura para los branchs](https://gitlab.com/groups/netpaymx/terminalizacion/-/wikis/Backend/Estandares/Nomenclatura-para-los-branchs).
2. Sigue los [estándares de nomenclatura para los commits](https://gitlab.com/groups/netpaymx/terminalizacion/-/wikis/Backend/Estandares/Nomenclatura-para-los-commits). 
3. Llena el [CHANGELOG](CHANGELOG.md) del repositorio al finalizar el proyecto previo a la generación del merge request, para mas detalle revisar la [documentación para llenado de changelog](https://gitlab.com/groups/netpaymx/terminalizacion/-/wikis/Backend/Uso-de-CHANGELOG.md).
4. Al crear un endpoint / proceso nuevo , se deberan incluir sus pruebas unitarias con JUnit en el apartado de test del servicio.
5. Generar merge request con el nombre del control de cambio y especificar en la descripción el imapcto en el servicio de una forma resumida y entendible. 

## Soporte 
* **[Code Owners](CODEOWNERS)**