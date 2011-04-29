import org.junit.*;
import java.util.*;
import play.test.*;
import models.*;

public class BasicTest extends UnitTest {


 @Before
    public void setup() {
        Fixtures.deleteAll();
    }

@Test
public void createAndRetrieveUser() {
    // Create a new user and save it
    new User("bob@gmail.com", "secret", "Bob").save();
    
    // Retrieve the user with e-mail address bob@gmail.com
    User bob = User.find("byEmail", "bob@gmail.com").first();
    
    // Test 
    assertNotNull(bob);
    assertEquals("Bob", bob.fullname);
}

@Test
public void tryConnectAsUser() {
    // Create a new user and save it
    new User("bob@gmail.com", "secret", "Bob").save();
    
    // Test 
    assertNotNull(User.connect("bob@gmail.com", "secret"));
    assertNull(User.connect("bob@gmail.com", "badpassword"));
    assertNull(User.connect("tom@gmail.com", "secret"));
}

@Test
public void postComments() {
    // Create a new user and save it
    User bob = new User("bob@gmail.com", "secret", "Bob").save();
 
    // Create a new post
    Post bobPost = new Post(bob, "My first post", "Hello world").save();
 
    // Post a first comment
    new Comment(bobPost, "Jeff", "Nice post").save();
    new Comment(bobPost, "Tom", "I knew that !").save();
 
    // Retrieve all comments
    List<Comment> bobPostComments = Comment.find("byPost", bobPost).fetch();
 
    // Tests
    assertEquals(2, bobPostComments.size());
 
    Comment firstComment = bobPostComments.get(0);
    assertNotNull(firstComment);
    assertEquals("Jeff", firstComment.author);
    assertEquals("Nice post", firstComment.content);
    assertNotNull(firstComment.postedAt);
 
    Comment secondComment = bobPostComments.get(1);
    assertNotNull(secondComment);
    assertEquals("Tom", secondComment.author);
    assertEquals("I knew that !", secondComment.content);
    assertNotNull(secondComment.postedAt);
}

@Test
public void useTheCommentsRelation() {
    // Create a new user and save it
    User bob = new User("bob@gmail.com", "secret", "Bob").save();
 
    // Create a new post
    Post bobPost = new Post(bob, "My first post", "Hello world").save();
 
    // Post a first comment
    bobPost.addComment("Jeff", "Nice post");
    bobPost.addComment("Tom", "I knew that !");
 
    // Count things
    assertEquals(1, User.count());
    assertEquals(1, Post.count());
    assertEquals(2, Comment.count());
 
    // Retrieve Bob's post
    bobPost = Post.find("byAuthor", bob).first();
    assertNotNull(bobPost);
 
    // Navigate to comments
    assertEquals(2, bobPost.comments.size());
    assertEquals("Jeff", bobPost.comments.get(0).author);
    
    // Delete the post
    bobPost.delete();
    
    // Check that all comments have been deleted
    assertEquals(1, User.count());
    assertEquals(0, Post.count());
    assertEquals(0, Comment.count());
}

}
