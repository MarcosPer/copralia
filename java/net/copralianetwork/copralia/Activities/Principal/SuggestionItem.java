package net.copralianetwork.copralia.Activities.Principal;

import android.os.Parcel;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;

/**
 * Created by marcos on 2/03/17.
 */

public class SuggestionItem implements SearchSuggestion {

    private String type;
    private String ID;
    private String name;
    private String desc;
    private String image;

    public SuggestionItem() {
    }

    public SuggestionItem(Parcel source) {
        this.ID = source.readString();
        this.type = source.readString();
        this.name = source.readString();
        this.desc = source.readString();
        this.image = source.readString();
    }



    @Override
    public String getBody() {
        return name;
    }

    public static final Creator<SuggestionItem> CREATOR = new Creator<SuggestionItem>() {
        @Override
        public SuggestionItem createFromParcel(Parcel in) {
            return new SuggestionItem(in);
        }
        @Override
        public SuggestionItem[] newArray(int size) {
            return new SuggestionItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type);
        dest.writeString(ID);
        dest.writeString(name);
        dest.writeString(desc);
        dest.writeString(image);
    }

    /* Getters and setters */
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return ID;
    }

    public void setId(String id) {
        this.ID = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

}
