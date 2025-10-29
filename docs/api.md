# DTO

## TimetableDTO

- same as table `timetable`

## ClassTimeDTO

- same as table `class_time`

## CourseDTO

- includes all columns of table `course`
- includes additional property: `classTimes: List<ClassTimeDTO>`

# 시간표 관리 기능(`/timetable`, 로그인 필요)

## 시간표 생성

- method & path: POST `/timetable`
- request body:
```json5
{
  "name": "string", // should not be whitespace
  "year": "int",
  "semester": "int" // should be one of 1,2,3,4
}
```
- status code:
  - 201: new timetable is created
  - 400: request body is not valid
  - 409: uniqueness collision on (user_id, name, year, semester)
- response body: `TimetableDTO`

## 시간표 목록 조회

- method & path: GET `/timetable`
- request body: none
- status code:
    - 200
- response body: `List<TimetableDTO>`

## 시간표 상세 조회

- method & path: GET `/timetable/:id`
- request body: none
- status code:
    - 200
    - 403: timetable with :id is not user's
    - 404: there is no timetable with :id
- response body: 
```json
{
  "timetable": "TimetableDTO",
  "course": "List<CourseDTO>",
  "credit": "int"
}
```

## 시간표 수정

- method & path: PATCH `/timetable/:id`
- request body:
```json5
{
  "name": "string", // should not be whitespace
}
```
- status code:
    - 200: the timetable is updated
    - 400: request body is not valid
    - 403: timetable with :id is not user's
    - 404: there is no timetable with :id
    - 409: uniqueness collision on (user_id, name, year, semester)
- response body: `TimetableDTO`

## 시간표 삭제

- method & path: PATCH `/timetable/:id`
- status code:
    - 204: the timetable is deleted
    - 403: timetable with :id is not user's
    - 404: there is no timetable with :id

# 서울대 수강신청 사이트에서 강의 정보 가져오기

- method & path: POST `/fetch/course`
- status code:
    - 204
    - 500: some errors
