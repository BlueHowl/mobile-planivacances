package be.helmo.planivacances.view.auth

import be.helmo.planivacances.view.auth.interfaces.IAuthService

interface AuthService : IAuthService{

    /*@GET("users")
    suspend fun getUsers(): Response<MutableList<User>>

    @GET("posts/{num}")
    suspend fun getPostById(@Path("num") num : Int): Response<Post>

    @GET("comments")
    suspend fun getCommentsByPost(@Query("postId") postId : Int): Response<MutableList<Comment>>

    @POST("posts")
    suspend fun createPost(@Body post: Post): Response<Post>*/
}