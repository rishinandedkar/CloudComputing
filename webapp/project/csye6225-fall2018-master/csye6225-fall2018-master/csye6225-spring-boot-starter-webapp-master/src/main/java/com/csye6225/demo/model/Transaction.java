package com.csye6225.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value = {"id"},
        allowGetters = true)

public class Transaction {

	@Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private String transactionId;

    @Column(length = 10)
    private String description;
    
    private String merchant;
    private String amount;
    private String date;
    private String category;


    @OneToMany(mappedBy = "transaction", cascade = CascadeType.REMOVE)
    private List<ReceiptUpload> receipts = new ArrayList<>();
    
   
    @ManyToOne
    private User user;

    public Transaction() {

    }

    public Transaction(String description) {
        this.description=description;
    }


    

    public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMerchant() {
		return merchant;
	}

	public void setMerchant(String merchant) {
		this.merchant = merchant;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

	public List<ReceiptUpload> getReceipts() {
		return receipts;
	}

	public void setReceipts(List<ReceiptUpload> receipts) {
		this.receipts = receipts;
	}
    
    
    
}
