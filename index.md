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
/screenshots/notesKeeper1.jpg
<img src=https://github.com/iamShekharGH/NotesKeeper/blob/main/screenshots/notesKeeper1.jpg width=200 /><img src=https://github.com/iamShekharGH/NotesKeeper/blob/main/screenshots/notesKeeper3.jpg width=200 /><img src=https://github.com/iamShekharGH/NotesKeeper/blob/main/screenshots/notesKeeper4.jpg width=200 />
  
### Add/Edit Note
<img src=https://github.com/iamShekharGH/NotesKeeper/blob/main/screenshots/notesKeeper5.jpg width=200 /><img src=https://github.com/iamShekharGH/NotesKeeper/blob/main/screenshots/notesKeeper8.jpg width=200 /><img src=https://github.com/iamShekharGH/NotesKeeper/blob/main/screenshots/notesKeeper10.jpg width=200 /><img src=https://github.com/iamShekharGH/NotesKeeper/blob/main/screenshots/notesKeeper13.jpg width=200 />

### Set Reminder
<img src=https://github.com/iamShekharGH/NotesKeeper/blob/main/screenshots/notesKeeper7.jpg width=200 /><img src=https://github.com/iamShekharGH/NotesKeeper/blob/main/screenshots/notesKeeper9.jpg width=200 />
### Menu Options like sort,Delete all Notes.
<img src=https://github.com/iamShekharGH/NotesKeeper/blob/main/screenshots/notesKeeper11.jpg width=200 /><img src=https://github.com/iamShekharGH/NotesKeeper/blob/main/screenshots/notesKeeper12.jpg width=200 />

### List and search
 <img src=https://github.com/iamShekharGH/NotesKeeper/blob/main/screenshots/notesKeeper2.jpg width=200 /><img src=https://github.com/iamShekharGH/NotesKeeper/blob/main/screenshots/notesKeeper14.jpg width=200 /><img src=https://github.com/iamShekharGH/NotesKeeper/blob/main/screenshots/notesKeeper15.jpg width=200 /><img src=https://github.com/iamShekharGH/NotesKeeper/blob/main/screenshots/notesKeeper16.jpg width=200 /><img src=https://github.com/iamShekharGH/NotesKeeper/blob/main/screenshots/notesKeeper6.jpg width=200 />


## Welcome to GitHub Pages

You can use the [editor on GitHub](https://github.com/iamShekharGH/NotesKeeper/edit/gh-pages/index.md) to maintain and preview the content for your website in Markdown files.

Whenever you commit to this repository, GitHub Pages will run [Jekyll](https://jekyllrb.com/) to rebuild the pages in your site, from the content in your Markdown files.

### Markdown

Markdown is a lightweight and easy-to-use syntax for styling your writing. It includes conventions for

```markdown
Syntax highlighted code block

# Header 1
## Header 2
### Header 3

- Bulleted
- List

1. Numbered
2. List

**Bold** and _Italic_ and `Code` text

[Link](url) and ![Image](src)
```

For more details see [GitHub Flavored Markdown](https://guides.github.com/features/mastering-markdown/).

### Jekyll Themes

Your Pages site will use the layout and styles from the Jekyll theme you have selected in your [repository settings](https://github.com/iamShekharGH/NotesKeeper/settings/pages). The name of this theme is saved in the Jekyll `_config.yml` configuration file.

### Support or Contact

Having trouble with Pages? Check out our [documentation](https://docs.github.com/categories/github-pages-basics/) or [contact support](https://support.github.com/contact) and we’ll help you sort it out.
