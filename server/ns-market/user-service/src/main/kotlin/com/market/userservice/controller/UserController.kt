package com.market.userservice.controller

import com.market.userservice.model.*
import com.market.userservice.service.UserService
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.core.io.ClassPathResource
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.*
import java.io.File

@RestController
@RequestMapping("/api/v1/users")
class UserController (
    private val userService: UserService,
){

    @PostMapping("/signup")
    suspend fun signup(@RequestBody request: SignUpRequest){
        userService.signUp(request)
    }

    @PostMapping("/signin")
    suspend fun signIn(@RequestBody singInRequest: SignInRequest): SignInResponse {
        return userService.signIn(singInRequest)
    }

    @DeleteMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun logout(@AuthToken token: String){
        userService.logout(token)
    }

    @GetMapping("/me")
    suspend fun get(
        @AuthToken token: String
    ) : MeResponse {
        return MeResponse(userService.getByToken(token))
    }

    @GetMapping("/{userId}/username")
    suspend fun getUserName(@PathVariable userId: Long): Map<String, String>{
        return mapOf("reporter" to userService.get(userId).username)
    }

    @PostMapping("/{id}", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    suspend fun edit(
        @PathVariable id:Long,
        @ModelAttribute request: UserEditRequest,
        @AuthToken token: String,
        @RequestPart("profileUrl") filePart: FilePart,
    ){
        val orgFIlename = filePart.filename()
        var filename: String? = null
        if(orgFIlename.isNotEmpty()){
            val ext = orgFIlename.substring(orgFIlename.lastIndexOf(".") +1) //확장자 구하는것
            filename = "${id}.${ext}"


            //resources/images/1.jpg 예시
            val file = File(ClassPathResource("/images").file , filename)
            filePart.transferTo(file).awaitSingleOrNull()

        }
        userService.edit(token, request.username, filename)
    }

    @GetMapping("/test1")
    suspend fun test1() : String{
        return "it's ok"
    }

}