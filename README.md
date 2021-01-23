# Waypoint
Built by Team 200
- David Turton
- Jack Craig
- Jerome Grubb
- Julia Harrison
- Kenzie Tandun
- Rchi Lgutu
-----
Basic project template using Gradle, NPM, Spring Boot, Vue and Gitlab CI. Remember to set up y
our Gitlab Ci server (refer to the student guide for instructions).

### Basic Project Structure
- client/src Frontend source code (JS - Vue)
- client/public publicly accesable web assets
- client/dist Frontend production build

- server/src Backend source code (Java - Spring)
- server/out Backend production build

### How to run
##### Client (Frontend/GUI)
`cd client`
`npm install`
`npm run serve`

Running on: http://localhost:9500/

##### Server (Backend/API)
`cd server`
`./gradlew bootRun`

Running on: http://localhost:9499/

##### Example User
There is a pre-loaded user account intended for use as an example.
<br> Email: user@example.com
<br> Password: password

##### Global Admin User Account
<br> Global Admin Username: admin@admin.com
<br> Global Admin Password: 233d4af9da9e4213aef811fc459da309

##### Obtaining Admin Credentials

If the admin user was not found in the database, it will be generated
automatically during the next backend startup. To obtain the credentials, simply
`SSH` to the VM and run the command: `sudo journalctl -u prodServer -n 100`.

### Reference
- [Spring Boot Docs](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
- [Spring JPA docs](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#preface)
- [Vue docs](https://vuejs.org/v2/guide/)

### Licenses

Licenses for libraries used in this project can be found at:

- [Frontend](./client/LICENSES/LICENSE.md)

- [Backend](./server/LICENCES/LICENSES.md)
