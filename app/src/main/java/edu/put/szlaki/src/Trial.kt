package edu.put.szlaki.src

class Trial {
    var id: String? = null
    var name: String? = null
    var length: String? = null
    var image: String? = null
    var comment: String? = null

    constructor(id: String, trialName: String, length: String, image: String, comment: String){
        this.id = id
        this.name = trialName
        this.length = length
        this.image = image
        this.comment = comment
    }
}