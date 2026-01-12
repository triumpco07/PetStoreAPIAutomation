package api.payload;

import java.util.List;

public class Pet_Pojo {
	private long id;
	private String name;
	private String status;
	private Category_Pojo category;
	private List<String> photoUrls;
	private List<Tag_Pojo> tags;
	
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Category_Pojo getCategory() {
		return category;
	}
	public void setCategory(Category_Pojo category) {
		this.category = category;
	}
	public List<String> getPhotoUrls() {
		return photoUrls;
	}
	public void setPhotoUrls(List<String> photoUrls) {
		this.photoUrls = photoUrls;
	}
	public List<Tag_Pojo> getTags() {
		return tags;
	}
	public void setTags(List<Tag_Pojo> tags) {
		this.tags = tags;
	}
	
	
	
	
	
}
