import gateway.*;
import model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.*;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class IntegrationTest
{
    public AdvertServiceImpl advertServImpl;
    public UserService userService;
    public AdvertsGateway advertsGateway;
    public UsersGateway userGateway;
    public UserAdapter userAdapter;
    public User user;

    @BeforeEach
    public void setUp()
    {
        userGateway = new UsersRepository();
        userAdapter = new UserAdapterImpl();
        advertsGateway = new AdvertsRepository();

        userService = new UserServiceImpl(userGateway , userAdapter);
        advertServImpl = new AdvertServiceImpl(userService, advertsGateway);
    }

    @DisplayName("For Seller ID, retrieve active/inactive adverts")
    @Test
     // This will cover the scenario's for Active and Inactive Adverts for the seller who has posted some adverts
     public void get_adverts_test()
     {
         long sellerId[] = {10, 11, 2, 3};
         for(int id = 0 ; id < sellerId.length; id++)
         {
             System.out.println("Seller Id is #####" + sellerId[id]);
             List<Advert> advertConsolidatedList = advertServImpl.getAdverts(sellerId[id]);
             ArrayList<Advert> activeAdvert = new ArrayList<Advert>();
             ArrayList<Advert> inActiveAdvert = new ArrayList<Advert>();

             for (int i = 0; i < advertConsolidatedList.size(); i++) {
                 if (advertConsolidatedList.get(i).isExpired())
                     inActiveAdvert.add(advertConsolidatedList.get(i));
                 else
                     activeAdvert.add(advertConsolidatedList.get(i));
             }
             System.out.println("Number of Active Advert  ## " + activeAdvert.size());
             System.out.println("Number of Active Advert  ## " + inActiveAdvert.size());

             /* This is for displaying the advert description
             if (activeAdvert.size() > 0) {
                 System.out.println("Number of Active Advert  ## " + activeAdvert.size());

                 for (int j = 0; j < activeAdvert.size(); j++) {
                     System.out.println("Advert Description is ## " + activeAdvert.get(j).getDescription());
                 }
             }

             if (inActiveAdvert.size() > 0) {
                 System.out.println("Number of Inactive Advert ## " + inActiveAdvert.size());
                 for (int k = 0; k < inActiveAdvert.size(); k++) {
                     System.out.println("Advert Description is ## " + inActiveAdvert.get(k).getDescription());
                 }
             }*/

             if (sellerId[id] == 10) // This seller has posted only 1 advert and it's active . 0 inactive adverts
             {
                 Assertions.assertEquals(1,activeAdvert.size(),"More Than 1 Active Advert is Displayed");
                 Assertions.assertEquals(0,inActiveAdvert.size(),"Inactive Adverts are being displayed");
             }

             if (sellerId[id] == 11) // This seller has posted only 2 advert and it's active . 0 inactive adverts
             {
                 Assertions.assertEquals(2,activeAdvert.size(),"More Than 2 Active Advert are Displayed");
                 Assertions.assertEquals(0,inActiveAdvert.size(),"Inactive Adverts are being displayed. It should be 0");
             }

             if (sellerId[id] == 2) // This seller has posted 3 advert, 2 are active and 1 inactive
             {
                 Assertions.assertEquals(2,activeAdvert.size(),"More Than 2 Active Advert are Displayed");
                 Assertions.assertEquals(1,inActiveAdvert.size(),"More Than 1 Active Advert are Displayed");
             }

             if (sellerId[id] == 3) // This seller has posted only 2 advert they are inactive
             {
                 Assertions.assertEquals(0,activeAdvert.size(),"Active Advert are Displayed. It should be 0");
                 Assertions.assertEquals(2,inActiveAdvert.size(),"Wrong number of Inactive Adverts are being displayed");
             }
         }
     }

    @DisplayName("Get Adverts for new Seller ID")
    @Test
    /*
    For this scenario, seller ID 4 has been created which hasn't posted any advert.
    Expectation is zero adverts should be returned
    */
     public void get_Adverts_New_Seller_id_Test()
     {
         List<Advert> advertConsolidatedList = advertServImpl.getAdverts(4);
         Assertions.assertEquals(0, advertConsolidatedList.size(), "Adverts are getting displayed for new Seller");
     }

    @DisplayName("Re-post for Free - All conditions are being met")
    @Test
    public void repost_Advert_For_Free_Test()
    {
        /*
        For Re-post the advert for free, below conditions should be met
        - Advert Id should be present
        - User ID associated with the advert should be present
        - Advert is expired
        - User is private
        - No Active adverts associated with the user ID
        User ID 1 is not private
        User ID 2 is private and has got active adverts
        User ID 3 is private and has got zero active adverts - This meets the above conditions
        */
        //Assertions.assertTrue(advertServImpl.canRepostForFree(9),"Not able to Re-post the advert for free");
        long advertID = 9;
        long sellerID = 3;
        boolean isExpired = true;
        List<Advert> advertListBeforeChange = advertServImpl.getAdverts(sellerID);

        for(int i = 0 ; i < advertListBeforeChange.size(); i++)
        {
            System.out.println("Before making the Change");
            System.out.println("Advert ID is ## " + advertListBeforeChange.get(i).getId());
            System.out.println("Is Advert Expired ? " + advertListBeforeChange.get(i).isExpired());
        }

        advertServImpl.repostForFree(advertID);
        List<Advert> advertListAfterChange = advertServImpl.getAdverts(sellerID);
        for(int l = 0 ; l < advertListAfterChange.size(); l++)
        {
            if(advertListAfterChange.get(l).getId() == advertID) {
                isExpired = advertListAfterChange.get(l).isExpired();
                System.out.println("After reposting Advert ID " + advertID + ". Is Advert Expired? " + isExpired);
                break;
            }
        }
        Assertions.assertFalse(isExpired,"Not able to repost the advert for Free");
    }

    @DisplayName("Not able to Re-post for Free - User ID is not private")
    @Test
    public void canNot_Repost_Advert_For_Free_Test_UserId_Is_Not_Private()
    {
        // User ID 1 is not private . This fails one of the conditions for re-posting the advert for free
        Throwable exception = assertThrows(IllegalStateException.class,
                ()->{advertServImpl.repostForFree(1);} );
        Assertions.assertTrue(exception.getMessage().contains("Cannot repost this advert for free"),"Able to re-post the advert for free");
    }

    @DisplayName("Not able to Re-post for Free - Has got some active Adverts")
    @Test
    public void canNot_Repost_Advert_For_Free_Test_Has_Got_Some_Active_Adverts()
    {
        // User ID 2 is private and has got active adverts. This fails one of the conditions for re-posting the advert for free
        Throwable exception = assertThrows(IllegalStateException.class,
                ()->{advertServImpl.repostForFree(6);} );
        Assertions.assertTrue(exception.getMessage().contains("Cannot repost this advert for free"),"Able to re-post the advert for free");
    }

    /* This scenario can happen in case user has got 2 active sessions.
     In one session he/she is amending the advert and from another session advert Id is deleted */
    @DisplayName("Not able to Re-post for Free - Exception thrown for missing Advert ID")
    @Test
    public void canNot_Repost_Advert_For_Free_Test_Missing_AdvertID()
    {
        Throwable exception = assertThrows(NoSuchElementException.class,
                ()->{advertServImpl.repostForFree(50);} );
        Assertions.assertTrue(exception.getMessage().contains("not found"),"Able to re-post the advert for free");
    }

    /* This scenario can happen in case user has got 2 active sessions.
     In one session he/she is amending the advert and from another session account is de-activated*/
    @DisplayName("Not able to Re-post for Free - Exception thrown for missing User ID")
    @Test
    public void canNot_Repost_Advert_For_Free_Test_Missing_UserID()
    {
        //Advert with id 11 created for userID 5. This userID doesn't exists
        Throwable exception = assertThrows(IllegalStateException.class,
                ()->{advertServImpl.repostForFree(11);} );
        Assertions.assertTrue(exception.getMessage().contains("for free"),"Able to re-post the advert for free");
    }
}
