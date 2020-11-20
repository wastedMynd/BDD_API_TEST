import org.junit.runner.RunWith;
import org.junit.runners.Suite;
@RunWith(Suite.class)
@Suite.SuiteClasses({TestDogApi.class, TestPetStoreSwaggerApi.class})
public class ApiTestSuite{}
// note
// all tests fail, if you are not connected to the internet!