## Service
- 백그라운드에서 오랫동안 돌아야하는 작업을 수행할 때 사용
- InterProcess Communication(IPC) 수행 가능


  #### 종류
  ##### 1. Foreground
  유저에게 백그라운드에서 어떤 일이 일어나는지 알려준다. 단, 알림(notification)을 표시해야한다.
  ex) 음악 재생 애플리케이션

  ##### 2. Background
  유저는 알 수 없는 작업을 수행한다.
  ex) 이미지 전송 시 압축과정

  ##### 3. Bound
  bindService()를 사용하여 1개 이상의 애플리케이션 구성 요소를 서비스에 바인딩한다.
  - binding : 이름을 속성에 연관시키는 과정
  - 바인딩이 끝나면 서비스도 종료된다.

## IntentService
- Service 클래스의 하위 클래스
- on-demand request(=Intent)를 처리하기 위해 work queue process 패턴을 사용
- 고객 요청 받음 → 서비스 시작 → 작업 처리 → 서비스 종료

---

-- | Service | IntentService
:--:|:--:|:--:
Usage | UI, 오랜 작업을 수행하지 않을 때 | 오랜 시간 백그라운드에서 수행 + 메인 스레드와 통신 없을 때
How to Start | onStartService() | Context.startService(Intent)
Stop Service | stopService(), stopSelf() | 작업이 끝나면 자동 종료
Running Thread | 메인 스레드 | 분리된 작업 스레드
Triggering Thread | 어느 스레드에서든 가능 | 메인 스레드에서만 가능
Main Thread blocking | 메인 스레드에서 수행하므로 사용 불가 | 큐에 작업을 넣어 수행하므로 메인 스레드에 영향 없음
