package space.exys.yabs.model

data class Account(

        val ownerFirstName: String,
        val ownerLastName: String,
        val id: AccountId = AccountId.random()
)