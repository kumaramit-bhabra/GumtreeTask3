import gateway.AdvertsGateway;
import gateway.AdvertsRepository;
import gateway.UsersGateway;
import gateway.UsersRepository;
import model.Advert;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.*;

import java.util.ArrayList;
import java.util.List;

public class IntegrationTest
{
    public AdvertServiceImpl advertServImpl;
    public UserService userService;
    public AdvertsGateway advertsGateway;
    public UsersGateway userGateway;
    public UserAdapter userAdapter;
    public User user;


    @BeforeEach
    public  void setUp()
    {
        userGateway = new UsersRepository();
        userAdapter = new UserAdapterImpl();
        advertsGateway = new AdvertsRepository();

        userService = new UserServiceImpl(userGateway , userAdapter);
        advertServImpl = new AdvertServiceImpl(userService, advertsGateway);
    }

     @Test
     public void display_active_advert_greater_than_one()
     {

         List<Advert> advertConsolidatedList = advertServImpl.getAdverts(1);
         ArrayList<Advert> activeAdvert = new ArrayList<Advert>();
         ArrayList<Advert> inActiveAdvert = new ArrayList<Advert>();

         for(int i = 0; i < advertConsolidatedList.size(); i++ )
         {
             if (advertConsolidatedList.get(i).isExpired())
             {
                 inActiveAdvert.add(advertConsolidatedList.get(i));
                 continue;
             }
             else
             {
                 activeAdvert.add(advertConsolidatedList.get(i));
                 System.out.println("UID" + advertConsolidatedList.get(i).getUserId());
             }
         }

         if(activeAdvert.size() > 0)
         {
             System.out.println("Number of Active Advert  ## " +activeAdvert.size());
             for (int j = 0 ; j <activeAdvert.size(); j++)
             {
                 System.out.println("Advert Description is ## "+activeAdvert.get(j).getDescription());
             }
         }

         if(inActiveAdvert.size() > 0)
         {
             System.out.println("Number of Inactive Advert ## " +inActiveAdvert.size());
             for (int k = 0 ; k <inActiveAdvert.size(); k++)
             {
                 System.out.println("Advert Description is ## "+inActiveAdvert.get(k).getDescription());
             }
         }
     }
}
