# NotesKeeper

Hi,<p>
NotesKeeper is a very simple aap that lets you make notes. You can add labels, set reminders , save notes locally using room and also have a backup with firebase.
It is a fun project which honestly is not about the ass as much as it is about learning how to code in MVVM patterns.
I have followed MVVM pattern to write this aap. This is following the <a href="https://developer.android.com/jetpack/guide" >Guide to app Architecture.</a><p>
Dependencies used are :~

  - Dagger Hilt
  - Room DB
  - Navigation Component
  - Viewmodel
  - Kotlin coroutine
  - Gson
  - flexbox
  - firebase -> datastore, crashlitics, auth
  - Datastore (preference)
  - Glide
## Firebase (Cloud Firestore) Rules
```
rules_version = '2';
service cloud.firestore {
    match /databases/{database}/documents {
    
        match /testNote/{testNote}{
        
          allow read: if isLoggedIn()
          allow update,create: if authIsSame()
          allow delete: if isLoggedIn()          
        }
    }
    
    function isLoggedIn(){
    	return request.auth.uid != null
    }
    
    function authIsSame(){
     return request.auth.uid == request.resource.data.firebaseUserId
    }
    
} 
```
## Here are a few(a lot) screenshots:~<p>
  
### SignUp
<img src=/screenshots/notesKeeper1.jpg width=200 /><img src=/screenshots/notesKeeper3.jpg width=200 /><img src=/screenshots/notesKeeper4.jpg width=200 />
  
### Add/Edit Note
<img src=/screenshots/notesKeeper5.jpg width=200 /><img src=/screenshots/notesKeeper8.jpg width=200 /><img src=/screenshots/notesKeeper10.jpg width=200 /><img src=/screenshots/notesKeeper13.jpg width=200 />

### Set Reminder
<img src=/screenshots/notesKeeper7.jpg width=200 /><img src=/screenshots/notesKeeper9.jpg width=200 />
### Menu Options like sort,Delete all Notes.
<img src=/screenshots/notesKeeper11.jpg width=200 /><img src=/screenshots/notesKeeper12.jpg width=200 />

### List and search
 <img src=/screenshots/notesKeeper2.jpg width=200 /><img src=/screenshots/notesKeeper14.jpg width=200 /><img src=/screenshots/notesKeeper15.jpg width=200 /><img src=/screenshots/notesKeeper16.jpg width=200 /><img src=/screenshots/notesKeeper6.jpg width=200 />
