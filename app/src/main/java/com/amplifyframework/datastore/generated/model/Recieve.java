package com.amplifyframework.datastore.generated.model;


import androidx.core.util.ObjectsCompat;

import java.util.Objects;
import java.util.List;

/** This is an auto generated class representing the Recieve type in your schema. */
public final class Recieve {
  private final String file_ext;
  private final String file_url;
  private final String sender;
  private final String receiver;
  public String getFileExt() {
      return file_ext;
  }
  
  public String getFileUrl() {
      return file_url;
  }
  
  public String getSender() {
      return sender;
  }
  
  public String getReceiver() {
      return receiver;
  }
  
  private Recieve(String file_ext, String file_url, String sender, String receiver) {
    this.file_ext = file_ext;
    this.file_url = file_url;
    this.sender = sender;
    this.receiver = receiver;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      Recieve recieve = (Recieve) obj;
      return ObjectsCompat.equals(getFileExt(), recieve.getFileExt()) &&
              ObjectsCompat.equals(getFileUrl(), recieve.getFileUrl()) &&
              ObjectsCompat.equals(getSender(), recieve.getSender()) &&
              ObjectsCompat.equals(getReceiver(), recieve.getReceiver());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getFileExt())
      .append(getFileUrl())
      .append(getSender())
      .append(getReceiver())
      .toString()
      .hashCode();
  }
  
  public static FileExtStep builder() {
      return new Builder();
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(file_ext,
      file_url,
      sender,
      receiver);
  }
  public interface FileExtStep {
    FileUrlStep fileExt(String fileExt);
  }
  

  public interface FileUrlStep {
    SenderStep fileUrl(String fileUrl);
  }
  

  public interface SenderStep {
    ReceiverStep sender(String sender);
  }
  

  public interface ReceiverStep {
    BuildStep receiver(String receiver);
  }
  

  public interface BuildStep {
    Recieve build();
  }
  

  public static class Builder implements FileExtStep, FileUrlStep, SenderStep, ReceiverStep, BuildStep {
    private String file_ext;
    private String file_url;
    private String sender;
    private String receiver;
    @Override
     public Recieve build() {
        
        return new Recieve(
          file_ext,
          file_url,
          sender,
          receiver);
    }
    
    @Override
     public FileUrlStep fileExt(String fileExt) {
        Objects.requireNonNull(fileExt);
        this.file_ext = fileExt;
        return this;
    }
    
    @Override
     public SenderStep fileUrl(String fileUrl) {
        Objects.requireNonNull(fileUrl);
        this.file_url = fileUrl;
        return this;
    }
    
    @Override
     public ReceiverStep sender(String sender) {
        Objects.requireNonNull(sender);
        this.sender = sender;
        return this;
    }
    
    @Override
     public BuildStep receiver(String receiver) {
        Objects.requireNonNull(receiver);
        this.receiver = receiver;
        return this;
    }
  }
  

  public final class CopyOfBuilder extends Builder {
    private CopyOfBuilder(String fileExt, String fileUrl, String sender, String receiver) {
      super.fileExt(fileExt)
        .fileUrl(fileUrl)
        .sender(sender)
        .receiver(receiver);
    }
    
    @Override
     public CopyOfBuilder fileExt(String fileExt) {
      return (CopyOfBuilder) super.fileExt(fileExt);
    }
    
    @Override
     public CopyOfBuilder fileUrl(String fileUrl) {
      return (CopyOfBuilder) super.fileUrl(fileUrl);
    }
    
    @Override
     public CopyOfBuilder sender(String sender) {
      return (CopyOfBuilder) super.sender(sender);
    }
    
    @Override
     public CopyOfBuilder receiver(String receiver) {
      return (CopyOfBuilder) super.receiver(receiver);
    }
  }
  
}
