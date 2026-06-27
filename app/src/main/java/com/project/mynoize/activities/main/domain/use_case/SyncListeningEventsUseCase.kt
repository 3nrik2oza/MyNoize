package com.project.mynoize.activities.main.domain.use_case

import com.project.mynoize.data_collecting.data.repository.ListeningEventRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SyncListeningEventsUseCase(
    private val listeningEventRepository: ListeningEventRepository
) {
    suspend operator fun invoke() = withContext(Dispatchers.IO) {
        try{
            listeningEventRepository.uploadLocalListeningEvents()
        }catch (e: Exception){
            print(e)
        }
    }
}