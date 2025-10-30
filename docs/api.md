# DTO

## TimetableDTO

- same as table `timetables`

## ClassTimeDTO

- same as table `class_times`

## CourseDTO

- includes all columns of table `courses`
- includes additional property: `classTimes: List<ClassTimeDTO>`

## TimetableDetailsDTO

```kotlin
data class TimetableDetailsDTO(
    val timetable: TimetableDTO,
    val courses: List<CourseDTO>,
    val credits: Int
)
```

## PaginationMeta

```json5
{
  "nextId": "int",
  "size": "int",     // page size
  "total": "int",    // total items
  "hasNext": "bool"
}
```

## CourseSearchResponse

```json5
{
  "items": "List<CourseDTO>",
  "meta": "PaginationMeta"
}
```

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
- response body: `TimetableDetailsDTO`

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

- method & path: DELETE `/timetable/:id`
- status code:
    - 204: the timetable is deleted
    - 403: timetable with :id is not user's
    - 404: there is no timetable with :id

# 서울대 수강신청 사이트에서 강의 정보 가져오기

- method & path: POST `/fetch/course`
- status code:
    - 204
    - 500: some errors

# 강의 탐색 및 시간표 연동 기능

## 강의 검색

- method & path: GET `/course/search`
- query params:
    - year: int
    - semester: int
    - q: string
    - nextId: long
    - size: int
- status code:
    - 200
    - 400: invalid query parameters
    - 500: server error
- response body: `CourseSearchResponse`

## 시간표에 강의 추가

- method & path: POST `/timetable/:id/course`
- auth: required
- request body:
```json5
{
  "courseId": "string"
}
```
- validation rules:
    - timetable ownership check (403)
    - timetable or course existence check (404)
    - prevent duplicate addition (409: DUPLICATE_COURSE)
    - prevent overlapping class times (409: TIME_CONFLICT)
- status code:
    - 201: course added to timetable
    - 403: timetable with :id is not user's
    - 404: timetable or course not found
    - 409: time conflict or duplicate course
    - 500: server error
- response body: `TimetableDetailsDTO`

## 시간표에서 강의 삭제

- method & path: DELETE `/timetable/:id/course/:course_id`
- auth: required
- status code:
    - 204: the course is removed from timetable
    - 403: timetable with :id is not user's
    - 404: timetable or course not found
- response body: none
