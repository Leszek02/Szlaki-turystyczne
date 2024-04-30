package edu.put.szlaki.src

class Stage {
    var id: String? = null
    var name: String? = null
    var length: String? = null
    var time: String? = null

    constructor(id: String?, trialName: String, length: String, time: String){
        this.id = id
        this.name = trialName
        this.length = length
        this.time = time
    }
}