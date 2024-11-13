package com.puzzle.backend.common.oauth.service

import com.puzzle.backend.common.oauth.domain.Users
import com.puzzle.backend.common.oauth.enums.SocialType
import com.puzzle.backend.common.oauth.repository.UsersRepository
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CustomOAuth2UserService (
    private val usersRepository: UsersRepository
): DefaultOAuth2UserService(){
    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val oAuth2User = super.loadUser(userRequest)
        val type = SocialType.valueOf(userRequest.clientRegistration.registrationId.uppercase())
        return try {
            processUser(oAuth2User, type)
        }catch (e : IllegalArgumentException){
            throw OAuth2AuthenticationException("Unsupported provider")
        }
    }

    private fun processUser(oAuth2User: OAuth2User, type: SocialType): OAuth2User{
        val attr = type.convert(oAuth2User.attributes)

        when (val user = usersRepository.findBySocialTypeAndEmail(type.name, attr.email)) {
            null -> {
                val newUser = Users(
                    userName = attr.userName,
                    userImage = attr.image,
                    email = attr.email,
                    userId = 0,
                    socialType = type.name
                )
                usersRepository.save(newUser)
            }
            else -> {
                user.userImage = attr.image
                user.userName = attr.userName
            }
        }
        return oAuth2User
    }
}