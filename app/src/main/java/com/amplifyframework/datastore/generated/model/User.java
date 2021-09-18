package com.amplifyframework.datastore.generated.model;


import androidx.core.util.ObjectsCompat;

import java.util.Objects;
import java.util.List;

/** This is an auto generated class representing the User type in your schema. */
public final class User {
  private final String email;
  private final String userId;
  private final List<Sent> sent;
  private final List<Recieve> recieve;
  public String getEmail() {
      return email;
  }
  
  public String getUserId() {
      return userId;
  }
  
  public List<Sent> getSent() {
      return sent;
  }
  
  public List<Recieve> getRecieve() {
      return recieve;
  }
  
  private User(String email, String userId, List<Sent> sent, List<Recieve> recieve) {
    this.email = email;
    this.userId = userId;
    this.sent = sent;
    this.recieve = recieve;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      User user = (User) obj;
      return ObjectsCompat.equals(getEmail(), user.getEmail()) &&
              ObjectsCompat.equals(getUserId(), user.getUserId()) &&
              ObjectsCompat.equals(getSent(), user.getSent()) &&
              ObjectsCompat.equals(getRecieve(), user.getRecieve());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getEmail())
      .append(getUserId())
      .append(getSent())
      .append(getRecieve())
      .toString()
      .hashCode();
  }
  
  public static EmailStep builder() {
      return new Builder();
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(email,
      userId,
      sent,
      recieve);
  }
  public interface EmailStep {
    UserIdStep email(String email);
  }
  

  public interface UserIdStep {
    SentStep userId(String userId);
  }
  

  public interface SentStep {
    RecieveStep sent(List<Sent> sent);
  }
  

  public interface RecieveStep {
    BuildStep recieve(List<Recieve> recieve);
  }
  

  public interface BuildStep {
    User build();
  }
  

  public static class Builder implements EmailStep, UserIdStep, SentStep, RecieveStep, BuildStep {
    private String email;
    private String userId;
    private List<Sent> sent;
    private List<Recieve> recieve;
    @Override
     public User build() {
        
        return new User(
          email,
          userId,
          sent,
          recieve);
    }
    
    @Override
     public UserIdStep email(String email) {
        Objects.requireNonNull(email);
        this.email = email;
        return this;
    }
    
    @Override
     public SentStep userId(String userId) {
        Objects.requireNonNull(userId);
        this.userId = userId;
        return this;
    }
    
    @Override
     public RecieveStep sent(List<Sent> sent) {
        Objects.requireNonNull(sent);
        this.sent = sent;
        return this;
    }
    
    @Override
     public BuildStep recieve(List<Recieve> recieve) {
        Objects.requireNonNull(recieve);
        this.recieve = recieve;
        return this;
    }
  }
  

  public final class CopyOfBuilder extends Builder {
    private CopyOfBuilder(String email, String userId, List<Sent> sent, List<Recieve> recieve) {
      super.email(email)
        .userId(userId)
        .sent(sent)
        .recieve(recieve);
    }
    
    @Override
     public CopyOfBuilder email(String email) {
      return (CopyOfBuilder) super.email(email);
    }
    
    @Override
     public CopyOfBuilder userId(String userId) {
      return (CopyOfBuilder) super.userId(userId);
    }
    
    @Override
     public CopyOfBuilder sent(List<Sent> sent) {
      return (CopyOfBuilder) super.sent(sent);
    }
    
    @Override
     public CopyOfBuilder recieve(List<Recieve> recieve) {
      return (CopyOfBuilder) super.recieve(recieve);
    }
  }
  
}
