--
-- PostgreSQL database dump
--

-- Dumped from database version 10.14 (Debian 10.14-1.pgdg90+1)
-- Dumped by pg_dump version 14.11 (Homebrew)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.users (user_id, email, enabled, last_password_reset_date, password, username, creation_date, last_login) FROM stdin;
1	rufus	t	\N	$2a$08$lDnHPz7eUkSi6ao14Twuau08mzhWrL4kyZGGU5xfiGALO/Vxd5DOi	rufus	\N	\N
20	me	t	\N	$2a$08$lDnHPz7eUkSi6ao14Twuau08mzhWrL4kyZGGU5xfiGALO/Vxd5DOi	me	\N	\N
23	carrie	t	\N	$2a$08$lDnHPz7eUkSi6ao14Twuau08mzhWrL4kyZGGU5xfiGALO/Vxd5DOi	carrie	\N	\N
26	mom@test.com	t	\N	$2a$10$RFahccrkDPR1aUHfyS457Oc7n.2f7wU/sDUXQ.99wOvNL3xzaiPxK	mom@test.com	\N	\N
29	michelle	t	\N	$2a$08$lDnHPz7eUkSi6ao14Twuau08mzhWrL4kyZGGU5xfiGALO/Vxd5DOi	michelle	\N	\N
2	testname	\N	\N	password	testname	\N	\N
34	dad@userdetails.com	\N	\N	password	testname	\N	\N
500	testuser@testuser.com	t	\N	$2a$10$RFahccrkDPR1aUHfyS457Oc7n.2f7wU/sDUXQ.99wOvNL3xzaiPxK	testuser	\N	\N
501	adduser	t	\N	password	adduser	\N	\N
502	deleteuser	t	\N	password	deleteuser	\N	\N
\.


--
-- Data for Name: authority; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.authority (authority_id, name, user_id) FROM stdin;
1	ROLE_USER	1
2	ROLE_USER	20
3	ROLE_USER	23
4	ROLE_USER	26
5	ROLE_USER	500
6	ROLE_USER	29
\.


--
-- Data for Name: auto_tag_instructions; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.auto_tag_instructions (instruction_type, instruction_id, assign_tag_id, is_invert, search_terms, invert_filter) FROM stdin;
TAG	1000	346	f	9;88;368;372;374;375	\N
TEXT	1	301	f	Soup	false
TEXT	2	323	f	Crock-pot;Crockpot;Crock pot	false
TAG	3	346	f	371	\N
TAG	4	199	t	371	433
\.


--
-- Data for Name: campaigns; Type: TABLE DATA; Schema: public; Owner: bankuser
--

COPY public.campaigns (campaign_id, created_on, email, campaign, user_id) FROM stdin;
\.


--
-- Data for Name: list_layout; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.list_layout (layout_id, name, user_id, is_default) FROM stdin;
5	RoughGrained	\N	t
\.


--
-- Data for Name: list_category; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.list_category (category_id, name, layout_id, display_order, is_default) FROM stdin;
5	Meat	5	5	\N
6	Produce	5	6	\N
7	Dairy	5	7	\N
8	Dry	5	8	\N
9	Other	5	9	\N
10	Frozen	5	10	\N
1041	Not (yet) categorized	5	999	t
\.


--
-- Data for Name: tag; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.tag (tag_id, description, name, tag_type, tag_type_default, is_verified, power, to_delete, replacement_tag_id, created_on, updated_on, category_updated_on, removed_on, is_group, user_id, internal_status, is_liquid, conversion_id, marker) FROM stdin;
1154	are cute	baby rutabegas	Ingredient	\N	\N	\N	f	\N	2021-04-11 07:47:21.046+00	\N	\N	\N	f	\N	1	\N	\N	\N
346	\N	Meat	TagType	\N	\N	\N	f	\N	2021-04-11 02:00:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
1	\N	green chili - preprepared	Ingredient	\N	\N	\N	f	\N	2021-04-11 07:45:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
2	dd	cheap 5	Rating	\N	\N	5	f	\N	2021-04-11 07:44:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
3	\N	salt and pepper shaker	NonEdible	\N	\N	\N	f	\N	2021-04-11 07:43:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
4	\N	prepared pie crust	Ingredient	\N	\N	\N	f	\N	2021-04-11 07:42:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
5	\N	big envelopes	NonEdible	\N	\N	\N	f	\N	2021-04-11 07:41:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
6	\N	duck	Ingredient	\N	\N	\N	f	\N	2021-04-11 07:40:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
7	\N	Finger Food	TagType	\N	\N	\N	f	\N	2021-04-11 07:39:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
8	\N	Skillet Dish	TagType	\N	\N	\N	f	\N	2021-04-11 07:38:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
10	\N	delete	Ingredient	\N	\N	\N	f	\N	2021-04-11 07:36:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
11	\N	Mexican	TagType	\N	\N	\N	f	\N	2021-04-11 07:35:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
12	\N	applesauce	Ingredient	\N	\N	\N	f	\N	2021-04-11 07:34:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
13	\N	rice	Ingredient	\N	\N	\N	f	\N	2021-04-11 07:33:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
15	\N	celery	Ingredient	\N	\N	\N	f	\N	2021-04-11 07:31:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
16	\N	onion	Ingredient	\N	\N	\N	f	\N	2021-04-11 07:30:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
17	\N	vegetable soup	Ingredient	\N	\N	\N	f	\N	2021-04-11 07:29:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
18	\N	cheddar cheese	Ingredient	\N	\N	\N	f	\N	2021-04-11 07:28:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
19	\N	garlic	Ingredient	\N	\N	\N	f	\N	2021-04-11 07:27:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
20	\N	cream	Ingredient	\N	\N	\N	f	\N	2021-04-11 07:26:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
21	\N	broccoli	Ingredient	\N	\N	\N	f	\N	2021-04-11 07:25:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
22	\N	artichoke hearts	Ingredient	\N	\N	\N	f	\N	2021-04-11 07:24:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
23	\N	white wine	Ingredient	\N	\N	\N	f	\N	2021-04-11 07:23:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
24	\N	Pasta	TagType	\N	\N	\N	f	\N	2021-04-11 07:22:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
25	\N	canned corn	Ingredient	\N	\N	\N	f	\N	2021-04-11 07:21:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
26	\N	dry red beans	Ingredient	\N	\N	\N	f	\N	2021-04-11 07:20:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
27	\N	prosciutto	Ingredient	\N	\N	\N	f	\N	2021-04-11 07:19:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
28	\N	ham hock	Ingredient	\N	\N	\N	f	\N	2021-04-11 07:18:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
29	\N	tabasco	Ingredient	\N	\N	\N	f	\N	2021-04-11 07:17:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
30	\N	ground lamb	Ingredient	\N	\N	\N	f	\N	2021-04-11 07:16:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
31	\N	cordon blue	Ingredient	\N	\N	\N	f	\N	2021-04-11 07:15:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
32	\N	lettuce	Ingredient	\N	\N	\N	f	\N	2021-04-11 07:14:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
33	\N	tomatoes	Ingredient	\N	\N	\N	f	\N	2021-04-11 07:13:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
34	\N	cucumber	Ingredient	\N	\N	\N	f	\N	2021-04-11 07:12:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
36	\N	feta cheese	Ingredient	\N	\N	\N	f	\N	2021-04-11 07:10:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
37	\N	feta cheese	Ingredient	\N	\N	\N	f	\N	2021-04-11 07:09:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
38	\N	salmon	Ingredient	\N	\N	\N	f	\N	2021-04-11 07:08:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
39	\N	delete	Ingredient	\N	\N	\N	f	\N	2021-04-11 07:07:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
40	\N	delete	Ingredient	\N	\N	\N	f	\N	2021-04-11 07:06:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
41	\N	peanuts	Ingredient	\N	\N	\N	f	\N	2021-04-11 07:05:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
42	\N	jelly	Ingredient	\N	\N	\N	f	\N	2021-04-11 07:04:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
44	\N	bread	Ingredient	\N	\N	\N	f	\N	2021-04-11 07:02:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
45	\N	oranges	Ingredient	\N	\N	\N	f	\N	2021-04-11 07:01:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
46	\N	cod	Ingredient	\N	\N	\N	f	\N	2021-04-11 07:00:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
47	\N	cayenne pepper	Ingredient	\N	\N	\N	f	\N	2021-04-11 06:59:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
48	\N	chives	Ingredient	\N	\N	\N	f	\N	2021-04-11 06:58:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
49	\N	risotto rice	Ingredient	\N	\N	\N	f	\N	2021-04-11 06:57:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
50	\N	saffron	Ingredient	\N	\N	\N	f	\N	2021-04-11 06:56:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
51	\N	mascarpone	Ingredient	\N	\N	\N	f	\N	2021-04-11 06:55:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
52	\N	tarragon	Ingredient	\N	\N	\N	f	\N	2021-04-11 06:54:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
53	\N	snow peas	Ingredient	\N	\N	\N	f	\N	2021-04-11 06:53:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
54	\N	caraway seeds	Ingredient	\N	\N	\N	f	\N	2021-04-11 06:52:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
55	\N	shallot	Ingredient	\N	\N	\N	f	\N	2021-04-11 06:51:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
56	\N	leek	Ingredient	\N	\N	\N	f	\N	2021-04-11 06:50:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
57	\N	bay leaf	Ingredient	\N	\N	\N	f	\N	2021-04-11 06:49:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
58	\N	fresh thyme	Ingredient	\N	\N	\N	f	\N	2021-04-11 06:48:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
59	\N	red potatoes	Ingredient	\N	\N	\N	f	\N	2021-04-11 06:47:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
60	\N	spinach	Ingredient	\N	\N	\N	f	\N	2021-04-11 06:46:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
61	\N	egg noodles	Ingredient	\N	\N	\N	f	\N	2021-04-11 06:45:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
62	\N	bread crumbs	Ingredient	\N	\N	\N	f	\N	2021-04-11 06:44:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
63	\N	sponge	NonEdible	\N	\N	\N	f	\N	2021-04-11 06:43:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
64	\N	cheap 4	Rating	\N	\N	4	f	\N	2021-04-11 06:42:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
65	\N	apples	Ingredient	\N	\N	\N	f	\N	2021-04-11 06:41:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
66	\N	muesli	Ingredient	\N	\N	\N	f	\N	2021-04-11 06:40:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
67	\N	milk	Ingredient	\N	\N	\N	f	\N	2021-04-11 06:39:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
68	\N	yogurt	Ingredient	\N	\N	\N	f	\N	2021-04-11 06:38:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
69	\N	eggs	Ingredient	\N	\N	\N	f	\N	2021-04-11 06:37:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
70	\N	creme fraiche	Ingredient	\N	\N	\N	f	\N	2021-04-11 06:36:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
71	\N	grated swiss cheese	Ingredient	\N	\N	\N	f	\N	2021-04-11 06:35:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
72	\N	pie crust	Ingredient	\N	\N	\N	f	\N	2021-04-11 06:34:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
73	\N	lardons	Ingredient	\N	\N	\N	f	\N	2021-04-11 06:33:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
74	\N	lardons	Ingredient	\N	\N	\N	f	\N	2021-04-11 06:32:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
75	\N	cucumbers	Ingredient	\N	\N	\N	f	\N	2021-04-11 06:31:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
76	\N	red pepper	Ingredient	\N	\N	\N	f	\N	2021-04-11 06:30:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
77	\N	white vinegar	Ingredient	\N	\N	\N	f	\N	2021-04-11 06:29:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
78	\N	red lentils	Ingredient	\N	\N	\N	f	\N	2021-04-11 06:28:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
79	\N	baking soda	Ingredient	\N	\N	\N	f	\N	2021-04-11 06:27:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
80	\N	chocolate chips	Ingredient	\N	\N	\N	f	\N	2021-04-11 06:26:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
81	\N	carrots	Ingredient	\N	\N	\N	f	\N	2021-04-11 06:25:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
82	\N	dry black beans	Ingredient	\N	\N	\N	f	\N	2021-04-11 06:24:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
83	\N	delete	Ingredient	\N	\N	\N	f	\N	2021-04-11 06:23:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
84	\N	yeast	Ingredient	\N	\N	\N	f	\N	2021-04-11 06:22:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
85	\N	salt and pepper	Ingredient	\N	\N	\N	f	\N	2021-04-11 06:21:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
86	\N	unused	TagType	\N	\N	\N	f	\N	2021-04-11 06:20:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
89	\N	merguez	Ingredient	\N	\N	\N	f	\N	2021-04-11 06:17:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
90	\N	frozen hamburgers	Ingredient	\N	\N	\N	f	\N	2021-04-11 06:16:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
91	\N	tortellini	Ingredient	\N	\N	\N	f	\N	2021-04-11 06:15:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
92	\N	pesto pasta sauce	Ingredient	\N	\N	\N	f	\N	2021-04-11 06:14:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
93	\N	saram wrap	NonEdible	\N	\N	\N	f	\N	2021-04-11 06:13:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
94	\N	dry cat food	NonEdible	\N	\N	\N	f	\N	2021-04-11 06:12:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
95	\N	crackers	Ingredient	\N	\N	\N	f	\N	2021-04-11 06:11:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
96	\N	brown cookies	Ingredient	\N	\N	\N	f	\N	2021-04-11 06:10:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
97	\N	pears	Ingredient	\N	\N	\N	f	\N	2021-04-11 06:09:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
98	\N	grapefruit	Ingredient	\N	\N	\N	f	\N	2021-04-11 06:08:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
99	\N	kalamata olives	Ingredient	\N	\N	\N	f	\N	2021-04-11 06:07:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
100	\N	plum tomato	Ingredient	\N	\N	\N	f	\N	2021-04-11 06:06:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
101	\N	fresh basil	Ingredient	\N	\N	\N	f	\N	2021-04-11 06:05:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
102	\N	capers	Ingredient	\N	\N	\N	f	\N	2021-04-11 06:04:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
103	\N	orange zest	Ingredient	\N	\N	\N	f	\N	2021-04-11 06:03:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
104	\N	lemon juice	Ingredient	\N	\N	\N	f	\N	2021-04-11 06:02:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
105	\N	fusilli	Ingredient	\N	\N	\N	f	\N	2021-04-11 06:01:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
106	\N	quail eggs	Ingredient	\N	\N	\N	f	\N	2021-04-11 06:00:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
107	\N	fresh dill	Ingredient	\N	\N	\N	f	\N	2021-04-11 05:59:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
108	\N	yellow bell pepper	Ingredient	\N	\N	\N	f	\N	2021-04-11 05:58:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
109	\N	asparagus	Ingredient	\N	\N	\N	f	\N	2021-04-11 05:57:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
110	\N	canned diced tomatoes	Ingredient	\N	\N	\N	f	\N	2021-04-11 05:56:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
111	\N	baby spinach	Ingredient	\N	\N	\N	f	\N	2021-04-11 05:55:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
112	\N	garam masala	Ingredient	\N	\N	\N	f	\N	2021-04-11 05:54:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
113	\N	canned chickpeas	Ingredient	\N	\N	\N	f	\N	2021-04-11 05:53:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
114	\N	couscous	Ingredient	\N	\N	\N	f	\N	2021-04-11 05:52:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
115	\N	rotisserie chicken	Ingredient	\N	\N	\N	f	\N	2021-04-11 05:51:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
116	\N	butternut squash	Ingredient	\N	\N	\N	f	\N	2021-04-11 05:50:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
117	\N	campanelle	Ingredient	\N	\N	\N	f	\N	2021-04-11 05:49:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
118	\N	leeks	Ingredient	\N	\N	\N	f	\N	2021-04-11 05:48:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
119	\N	fresh oregano	Ingredient	\N	\N	\N	f	\N	2021-04-11 05:47:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
120	\N	eggplant	Ingredient	\N	\N	\N	f	\N	2021-04-11 05:46:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
121	\N	penne	Ingredient	\N	\N	\N	f	\N	2021-04-11 05:45:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
122	\N	goat cheese	Ingredient	\N	\N	\N	f	\N	2021-04-11 05:44:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
123	\N	white rice	Ingredient	\N	\N	\N	f	\N	2021-04-11 05:43:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
124	\N	strip steaks	Ingredient	\N	\N	\N	f	\N	2021-04-11 05:42:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
125	\N	blue cheese	Ingredient	\N	\N	\N	f	\N	2021-04-11 05:41:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
126	\N	heavy cream	Ingredient	\N	\N	\N	f	\N	2021-04-11 05:40:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
127	\N	roasted red peppers	Ingredient	\N	\N	\N	f	\N	2021-04-11 05:39:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
128	\N	light brown sugar	Ingredient	\N	\N	\N	f	\N	2021-04-11 05:38:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
129	\N	mayonnaise	Ingredient	\N	\N	\N	f	\N	2021-04-11 05:37:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
130	\N	sun dried tomatoes	Ingredient	\N	\N	\N	f	\N	2021-04-11 05:36:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
131	\N	parmesan cheese	Ingredient	\N	\N	\N	f	\N	2021-04-11 05:35:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
132	\N	pine nuts	Ingredient	\N	\N	\N	f	\N	2021-04-11 05:34:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
133	\N	balsamic vinegar	Ingredient	\N	\N	\N	f	\N	2021-04-11 05:33:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
134	\N	farfalle	Ingredient	\N	\N	\N	f	\N	2021-04-11 05:32:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
135	\N	arugula	Ingredient	\N	\N	\N	f	\N	2021-04-11 05:31:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
136	\N	salami	Ingredient	\N	\N	\N	f	\N	2021-04-11 05:30:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
137	\N	granola bars	Ingredient	\N	\N	\N	f	\N	2021-04-11 05:29:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
138	\N	brioche	Ingredient	\N	\N	\N	f	\N	2021-04-11 05:28:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
139	\N	nutella	Ingredient	\N	\N	\N	f	\N	2021-04-11 05:27:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
140	\N	compote	Ingredient	\N	\N	\N	f	\N	2021-04-11 05:26:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
141	\N	toothpaste	NonEdible	\N	\N	\N	f	\N	2021-04-11 05:25:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
142	\N	Good For Picnics	TagType	\N	\N	\N	f	\N	2021-04-11 05:24:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
143	\N	Low Fat	TagType	\N	\N	\N	f	\N	2021-04-11 05:23:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
144	\N	window cleaner	NonEdible	\N	\N	\N	f	\N	2021-04-11 05:22:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
145	\N	mopping fluid	NonEdible	\N	\N	\N	f	\N	2021-04-11 05:21:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
146	\N	soft butter	Ingredient	\N	\N	\N	f	\N	2021-04-11 05:20:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
147	\N	english muffins	Ingredient	\N	\N	\N	f	\N	2021-04-11 05:19:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
148	\N	coffee filters	NonEdible	\N	\N	\N	f	\N	2021-04-11 05:18:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
149	\N	page protectors	NonEdible	\N	\N	\N	f	\N	2021-04-11 05:17:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
150	\N	blue pen	NonEdible	\N	\N	\N	f	\N	2021-04-11 05:16:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
151	\N	clementines	Ingredient	\N	\N	\N	f	\N	2021-04-11 05:15:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
152	\N	Low Carbohydrates	TagType	\N	\N	\N	f	\N	2021-04-11 05:14:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
153	\N	Low Salt	TagType	\N	\N	\N	f	\N	2021-04-11 05:13:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
154	\N	Low Calorie	TagType	\N	\N	\N	f	\N	2021-04-11 05:12:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
155	dd	unused	TagType	\N	\N	\N	f	\N	2021-04-11 05:11:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
156	\N	Low Glycemic Index	TagType	\N	\N	\N	f	\N	2021-04-11 05:10:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
158	\N	notused1	Ingredient	\N	\N	\N	f	\N	2021-04-11 05:08:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
159	\N	gizzards	Ingredient	\N	\N	\N	f	\N	2021-04-11 05:07:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
161	\N	shell pasta	Ingredient	\N	\N	\N	f	\N	2021-04-11 05:05:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
162	\N	sliced ham	Ingredient	\N	\N	\N	f	\N	2021-04-11 05:04:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
163	\N	light cream	Ingredient	\N	\N	\N	f	\N	2021-04-11 05:03:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
164	\N	canned white kidney beans	Ingredient	\N	\N	\N	f	\N	2021-04-11 05:02:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
165	\N	beef stock	Ingredient	\N	\N	\N	f	\N	2021-04-11 05:01:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
166	\N	spaghetti sauce	Ingredient	\N	\N	\N	f	\N	2021-04-11 05:00:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
167	\N	sweet potatoes	Ingredient	\N	\N	\N	f	\N	2021-04-11 04:59:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
168	\N	paprika	Ingredient	\N	\N	\N	f	\N	2021-04-11 04:58:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
169	\N	mustard powder	Ingredient	\N	\N	\N	f	\N	2021-04-11 04:57:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
170	\N	whipping cream	Ingredient	\N	\N	\N	f	\N	2021-04-11 04:56:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
171	\N	white pepper	Ingredient	\N	\N	\N	f	\N	2021-04-11 04:55:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
172	\N	walnuts	Ingredient	\N	\N	\N	f	\N	2021-04-11 04:54:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
173	\N	poulty seasoning	Ingredient	\N	\N	\N	f	\N	2021-04-11 04:53:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
174	\N	star spice	Ingredient	\N	\N	\N	f	\N	2021-04-11 04:52:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
175	\N	marshmallows	Ingredient	\N	\N	\N	f	\N	2021-04-11 04:51:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
176	\N	baking chocolate	Ingredient	\N	\N	\N	f	\N	2021-04-11 04:50:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
177	\N	powdered cocoa	Ingredient	\N	\N	\N	f	\N	2021-04-11 04:49:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
178	\N	confectioners sugar	Ingredient	\N	\N	\N	f	\N	2021-04-11 04:48:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
179	\N	corn syrup	Ingredient	\N	\N	\N	f	\N	2021-04-11 04:47:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
180	\N	oven cleaner	NonEdible	\N	\N	\N	f	\N	2021-04-11 04:46:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
181	\N	white sugar	Ingredient	\N	\N	\N	f	\N	2021-04-11 04:45:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
182	\N	vanilla extract	Ingredient	\N	\N	\N	f	\N	2021-04-11 04:44:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
183	\N	salsa	Ingredient	\N	\N	\N	f	\N	2021-04-11 04:43:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
184	\N	chili powder	Ingredient	\N	\N	\N	f	\N	2021-04-11 04:42:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
185	\N	oregano	Ingredient	\N	\N	\N	f	\N	2021-04-11 04:41:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
186	\N	vegetable stock	Ingredient	\N	\N	\N	f	\N	2021-04-11 04:40:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
187	\N	green bell pepper	Ingredient	\N	\N	\N	f	\N	2021-04-11 04:39:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
188	\N	canned kidney beans	Ingredient	\N	\N	\N	f	\N	2021-04-11 04:38:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
189	\N	canned black beans	Ingredient	\N	\N	\N	f	\N	2021-04-11 04:37:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
190	\N	canned tomato sauce	Ingredient	\N	\N	\N	f	\N	2021-04-11 04:36:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
191	\N	pumpkin puree	Ingredient	\N	\N	\N	f	\N	2021-04-11 04:35:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
192	\N	nutmeg	Ingredient	\N	\N	\N	f	\N	2021-04-11 04:34:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
193	\N	ground cloves	Ingredient	\N	\N	\N	f	\N	2021-04-11 04:33:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
194	\N	ground ginger	Ingredient	\N	\N	\N	f	\N	2021-04-11 04:32:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
195	\N	sandwich bags	NonEdible	\N	\N	\N	f	\N	2021-04-11 04:31:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
196	\N	loose tea	Ingredient	\N	\N	\N	f	\N	2021-04-11 04:30:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
198	\N	corn tortillas	Ingredient	\N	\N	\N	f	\N	2021-04-11 04:28:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
199	\N	Vegetarian	TagType	\N	\N	\N	f	\N	2021-04-11 04:27:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
200	\N	arrugula	Ingredient	\N	\N	\N	f	\N	2021-04-11 04:26:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
201	\N	gnocchi	Ingredient	\N	\N	\N	f	\N	2021-04-11 04:25:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
202	\N	gorgonzola	Ingredient	\N	\N	\N	f	\N	2021-04-11 04:24:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
203	\N	sour cream	Ingredient	\N	\N	\N	f	\N	2021-04-11 04:23:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
204	\N	Fish	TagType	\N	\N	\N	f	\N	2021-04-11 04:22:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
205	\N	potato chips	Ingredient	\N	\N	\N	f	\N	2021-04-11 04:21:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
206	\N	white bread	Ingredient	\N	\N	\N	f	\N	2021-04-11 04:20:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
207	\N	dill	Ingredient	\N	\N	\N	f	\N	2021-04-11 04:19:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
208	\N	lemon zest	Ingredient	\N	\N	\N	f	\N	2021-04-11 04:18:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
209	\N	canned garbanzo beans	Ingredient	\N	\N	\N	f	\N	2021-04-11 04:17:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
210	\N	canned tuna	Ingredient	\N	\N	\N	f	\N	2021-04-11 04:16:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
211	\N	scallion	Ingredient	\N	\N	\N	f	\N	2021-04-11 04:15:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
212	\N	fresh ginger	Ingredient	\N	\N	\N	f	\N	2021-04-11 04:14:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
213	\N	cranberries	Ingredient	\N	\N	\N	f	\N	2021-04-11 04:13:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
214	\N	kids shampoo	NonEdible	\N	\N	\N	f	\N	2021-04-11 04:12:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
215	\N	light bulb	NonEdible	\N	\N	\N	f	\N	2021-04-11 04:11:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
217	\N	canned sprouts	Ingredient	\N	\N	\N	f	\N	2021-04-11 04:09:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
218	\N	Quick To Prepare 3	Rating	\N	\N	3.10000000000000009	f	\N	2021-04-11 04:08:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
219	\N	Quick To Prepare 2	Rating	\N	\N	2.10000000000000009	f	\N	2021-04-11 04:07:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
220	\N	friday snack	Ingredient	\N	\N	\N	f	\N	2021-04-11 04:06:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
221	\N	diet coke	Ingredient	\N	\N	\N	f	\N	2021-04-11 04:05:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
222	\N	napkins	NonEdible	\N	\N	\N	f	\N	2021-04-11 04:04:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
223	\N	cat food	NonEdible	\N	\N	\N	f	\N	2021-04-11 04:03:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
224	\N	dog food	NonEdible	\N	\N	\N	f	\N	2021-04-11 04:02:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
225	\N	shampoo	NonEdible	\N	\N	\N	f	\N	2021-04-11 04:01:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
226	\N	shower soap	NonEdible	\N	\N	\N	f	\N	2021-04-11 04:00:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
227	\N	potatoes	Ingredient	\N	\N	\N	f	\N	2021-04-11 03:59:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
228	\N	smoked ham	Ingredient	\N	\N	\N	f	\N	2021-04-11 03:58:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
229	\N	pecans	Ingredient	\N	\N	\N	f	\N	2021-04-11 03:57:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
230	\N	kielbasa	Ingredient	\N	\N	\N	f	\N	2021-04-11 03:56:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
231	\N	canned whole tomatoes	Ingredient	\N	\N	\N	f	\N	2021-04-11 03:55:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
232	\N	cajun seasoning	Ingredient	\N	\N	\N	f	\N	2021-04-11 03:54:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
233	\N	ham steak	Ingredient	\N	\N	\N	f	\N	2021-04-11 03:53:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
234	\N	green split peas	Ingredient	\N	\N	\N	f	\N	2021-04-11 03:52:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
235	\N	kitty litter	NonEdible	\N	\N	\N	f	\N	2021-04-11 03:51:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
236	\N	toilet paper	NonEdible	\N	\N	\N	f	\N	2021-04-11 03:50:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
237	\N	peanut butter	Ingredient	\N	\N	\N	f	\N	2021-04-11 03:49:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
238	\N	tortillas	Ingredient	\N	\N	\N	f	\N	2021-04-11 03:48:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
239	\N	delete	Ingredient	\N	\N	\N	f	\N	2021-04-11 03:47:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
240	\N	delete	Ingredient	\N	\N	\N	f	\N	2021-04-11 03:46:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
241	\N	delete	Ingredient	\N	\N	\N	f	\N	2021-04-11 03:45:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
242	\N	ground coriander	Ingredient	\N	\N	\N	f	\N	2021-04-11 03:44:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
243	\N	ground cinnamon	Ingredient	\N	\N	\N	f	\N	2021-04-11 03:43:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
244	\N	Farro	Ingredient	\N	\N	\N	f	\N	2021-04-11 03:42:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
245	\N	frozen peas	Ingredient	\N	\N	\N	f	\N	2021-04-11 03:41:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
247	\N	fresh parsley	Ingredient	\N	\N	\N	f	\N	2021-04-11 03:39:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
248	\N	boursin cheese	Ingredient	\N	\N	\N	f	\N	2021-04-11 03:38:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
249	\N	fresh chives	Ingredient	\N	\N	\N	f	\N	2021-04-11 03:37:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
250	\N	white wine vinegar	Ingredient	\N	\N	\N	f	\N	2021-04-11 03:36:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
251	\N	stew meat	Ingredient	\N	\N	\N	f	\N	2021-04-11 03:35:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
252	\N	guiness beer	Ingredient	\N	\N	\N	f	\N	2021-04-11 03:34:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
253	\N	coffee	Ingredient	\N	\N	\N	f	\N	2021-04-11 03:33:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
254	\N	gum	NonEdible	\N	\N	\N	f	\N	2021-04-11 03:32:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
348	\N	butter	Ingredient	\N	\N	\N	f	\N	2021-04-11 01:58:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
349	\N	white mushrooms	Ingredient	\N	\N	\N	f	\N	2021-04-11 01:57:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
350	\N	flour	Ingredient	\N	\N	\N	f	\N	2021-04-11 01:56:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
351	\N	thyme	Ingredient	\N	\N	\N	f	\N	2021-04-11 01:55:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
352	\N	dijon mustard	Ingredient	\N	\N	\N	f	\N	2021-04-11 01:54:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
353	\N	diced canned tomatoes	Ingredient	\N	\N	\N	f	\N	2021-04-11 01:53:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
354	\N	parsley	Ingredient	\N	\N	\N	f	\N	2021-04-11 01:52:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
355	\N	red bell pepper	Ingredient	\N	\N	\N	f	\N	2021-04-11 01:51:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
356	\N	black pitted olives	Ingredient	\N	\N	\N	f	\N	2021-04-11 01:50:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
357	\N	lemon	Ingredient	\N	\N	\N	f	\N	2021-04-11 01:49:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
358	\N	cinnamon	Ingredient	\N	\N	\N	f	\N	2021-04-11 01:48:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
359	\N	honey	Ingredient	\N	\N	\N	f	\N	2021-04-11 01:47:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
360	\N	salt	Ingredient	\N	\N	\N	f	\N	2021-04-11 01:46:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
361	\N	brown sugar	Ingredient	\N	\N	\N	f	\N	2021-04-11 01:45:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
362	lll	delete	Ingredient	\N	\N	\N	f	\N	2021-04-11 01:44:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
363	\N	Kids Like It 3	Rating	\N	\N	3	f	\N	2021-04-11 01:43:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
364	\N	Kids Like It 2	Rating	\N	\N	2	f	\N	2021-04-11 01:42:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
365	\N	Kids Like It 1	Rating	\N	\N	1	f	\N	2021-04-11 01:41:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
366	ddd	delete	Ingredient	\N	\N	\N	f	\N	2021-04-11 01:40:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
255	\N	ground cumin	Ingredient	\N	\N	\N	f	\N	2021-04-11 03:31:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
256	\N	soy sauce	Ingredient	\N	\N	\N	f	\N	2021-04-11 03:30:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
257	\N	round steak	Ingredient	\N	\N	\N	f	\N	2021-04-11 03:29:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
258	\N	beef chuck roast	Ingredient	\N	\N	\N	f	\N	2021-04-11 03:28:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
259	\N	dried marjoram	Ingredient	\N	\N	\N	f	\N	2021-04-11 03:27:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
260	\N	black-eyed peas	Ingredient	\N	\N	\N	f	\N	2021-04-11 03:26:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
261	\N	red wine vinegar	Ingredient	\N	\N	\N	f	\N	2021-04-11 03:25:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
262	\N	fresh mint	Ingredient	\N	\N	\N	f	\N	2021-04-11 03:24:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
263	\N	pistachios	Ingredient	\N	\N	\N	f	\N	2021-04-11 03:23:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
264	\N	boxed gravy	Ingredient	\N	\N	\N	f	\N	2021-04-11 03:22:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
265	\N	trash bags	NonEdible	\N	\N	\N	f	\N	2021-04-11 03:21:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
266	\N	delete	Ingredient	\N	\N	\N	f	\N	2021-04-11 03:20:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
267	\N	Christmas	TagType	\N	\N	\N	f	\N	2021-04-11 03:19:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
268	\N	Thanksgiving	TagType	\N	\N	\N	f	\N	2021-04-11 03:18:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
269	\N	sliced sausage	Ingredient	\N	\N	\N	f	\N	2021-04-11 03:17:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
270	\N	dish soap	NonEdible	\N	\N	\N	f	\N	2021-04-11 03:16:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
271	\N	allspice	Ingredient	\N	\N	\N	f	\N	2021-04-11 03:15:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
272	\N	whole cloves	Ingredient	\N	\N	\N	f	\N	2021-04-11 03:14:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
273	\N	cider	Ingredient	\N	\N	\N	f	\N	2021-04-11 03:13:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
274	\N	unsweetened chocolate	Ingredient	\N	\N	\N	f	\N	2021-04-11 03:12:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
275	\N	bag for Christmas Hats	NonEdible	\N	\N	\N	f	\N	2021-04-11 03:11:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
276	\N	envelopes	NonEdible	\N	\N	\N	f	\N	2021-04-11 03:10:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
277	\N	romano cheese	Ingredient	\N	\N	\N	f	\N	2021-04-11 03:09:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
278	\N	sherry	Ingredient	\N	\N	\N	f	\N	2021-04-11 03:08:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
279	\N	gruyere cheese	Ingredient	\N	\N	\N	f	\N	2021-04-11 03:07:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
280	\N	radishes	Ingredient	\N	\N	\N	f	\N	2021-04-11 03:06:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
281	\N	sea salt	Ingredient	\N	\N	\N	f	\N	2021-04-11 03:05:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
282	\N	baguette	Ingredient	\N	\N	\N	f	\N	2021-04-11 03:04:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
283	\N	paper towels	NonEdible	\N	\N	\N	f	\N	2021-04-11 03:03:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
284	\N	jalapeno pepper	Ingredient	\N	\N	\N	f	\N	2021-04-11 03:02:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
285	\N	unsalted cashews	Ingredient	\N	\N	\N	f	\N	2021-04-11 03:01:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
286	\N	curry powder	Ingredient	\N	\N	\N	f	\N	2021-04-11 03:00:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
287	\N	fresh cilantro	Ingredient	\N	\N	\N	f	\N	2021-04-11 02:59:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
288	\N	fresh tarragon	Ingredient	\N	\N	\N	f	\N	2021-04-11 02:58:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
289	\N	turnips	Ingredient	\N	\N	\N	f	\N	2021-04-11 02:57:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
293	\N	baking powder	Ingredient	\N	\N	\N	f	\N	2021-04-11 02:53:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
294	\N	powdered sugar	Ingredient	\N	\N	\N	f	\N	2021-04-11 02:52:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
295	\N	chocolate bar	Ingredient	\N	\N	\N	f	\N	2021-04-11 02:51:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
296	\N	fennel seeds	Ingredient	\N	\N	\N	f	\N	2021-04-11 02:50:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
297	\N	cardamon	Ingredient	\N	\N	\N	f	\N	2021-04-11 02:49:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
298	\N	square baking dish	NonEdible	\N	\N	\N	f	\N	2021-04-11 02:48:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
299	\N	dry wipes	NonEdible	\N	\N	\N	f	\N	2021-04-11 02:47:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
300	\N	Healthy 5	Rating	\N	\N	5	f	\N	2021-04-11 02:46:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
301	\N	Soup	TagType	\N	\N	\N	f	\N	2021-04-11 02:45:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
302		Halal	TagType	\N	\N	\N	f	\N	2021-04-11 02:44:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
303		Kosher	TagType	\N	\N	\N	f	\N	2021-04-11 02:43:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
304	\N	unused	TagType	\N	\N	\N	f	\N	2021-04-11 02:42:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
307	\N	frozen green beans	Ingredient	\N	\N	\N	f	\N	2021-04-11 02:39:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
308	\N	bay leaves	Ingredient	\N	\N	\N	f	\N	2021-04-11 02:38:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
309	\N	dried thyme	Ingredient	\N	\N	\N	f	\N	2021-04-11 02:37:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
310	\N	fresh pumpkin	Ingredient	\N	\N	\N	f	\N	2021-04-11 02:36:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
311	\N	pork roast	Ingredient	\N	\N	\N	f	\N	2021-04-11 02:35:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
312	\N	diced green chilis	Ingredient	\N	\N	\N	f	\N	2021-04-11 02:34:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
314	\N	kuggin	NonEdible	\N	\N	\N	f	\N	2021-04-11 02:32:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
315	\N	cheap 2	Rating	\N	\N	2	f	\N	2021-04-11 02:31:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
316	\N	Quick To Prepare 5	Rating	\N	\N	5	f	\N	2021-04-11 02:30:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
317	\N	Quick To Prepare 4	Rating	\N	\N	4	f	\N	2021-04-11 02:29:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
318	\N	sesame oil	Ingredient	\N	\N	\N	f	\N	2021-04-11 02:28:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
319	\N	Elegance 5	Rating	\N	\N	5	f	\N	2021-04-11 02:27:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
320		Main Dish	DishType	\N	\N	\N	f	\N	2021-04-11 02:26:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
321	\N	Yummy 4	Rating	\N	\N	4	f	\N	2021-04-11 02:25:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
322	\N	Yummy 3	Rating	\N	\N	3	f	\N	2021-04-11 02:24:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
323	rr	crockpot	TagType	\N	\N	\N	f	\N	2021-04-11 02:23:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
324	\N	Yummy 5	Rating	\N	\N	5	f	\N	2021-04-11 02:22:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
325	\N	Yummy 1	Rating	\N	\N	1	f	\N	2021-04-11 02:21:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
326	\N	Yummy 2	Rating	\N	\N	2	f	\N	2021-04-11 02:20:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
327	\N	Kids Like It 5	Rating	\N	\N	5	f	\N	2021-04-11 02:19:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
328	\N	Kids Like It 4	Rating	\N	\N	4	f	\N	2021-04-11 02:18:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
329	\N	Ease of Prep 5	Rating	\N	\N	5	f	\N	2021-04-11 02:17:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
330	\N	Quick To Table 5	Rating	\N	\N	5	f	\N	2021-04-11 02:16:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
331	dd	Type TAG	TagType	\N	\N	\N	f	\N	2021-04-11 02:15:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
333	\N	Appetizer	DishType	t	\N	\N	f	\N	2021-04-11 02:13:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
334	\N	black pepper	Ingredient	\N	\N	\N	f	\N	2021-04-11 02:12:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
335	\N	red pepper flakes	Ingredient	\N	\N	\N	f	\N	2021-04-11 02:11:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
336	\N	olive oil	Ingredient	\N	\N	\N	f	\N	2021-04-11 02:10:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
337	\N	egg	Ingredient	\N	\N	\N	f	\N	2021-04-11 02:09:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
338	\N	canned white beans	Ingredient	\N	\N	\N	f	\N	2021-04-11 02:08:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
339	\N	canned tomatoes	Ingredient	\N	\N	\N	f	\N	2021-04-11 02:07:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
340	\N	sausage	Ingredient	\N	\N	\N	f	\N	2021-04-11 02:06:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
341	\N	tomato paste	Ingredient	\N	\N	\N	f	\N	2021-04-11 02:05:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
342	\N	zucchini	Ingredient	\N	\N	\N	f	\N	2021-04-11 02:04:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
343	\N	cumin	Ingredient	\N	\N	\N	f	\N	2021-04-11 02:03:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
344	rrr	cheap 3	Rating	\N	\N	3	f	\N	2021-04-11 02:02:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
345	\N	laughing cow cheese	Ingredient	\N	\N	\N	f	\N	2021-04-11 02:01:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
347	\N	vegetable oil	Ingredient	\N	\N	\N	f	\N	2021-04-11 01:59:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
387	\N	Difficulty	Rating	\N	\N	\N	f	\N	2021-04-11 01:19:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
395	\N	Elegance 4	Rating	\N	\N	4	f	\N	2021-04-11 01:11:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
396	\N	Elegance 3	Rating	\N	\N	3	f	\N	2021-04-11 01:10:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
397	\N	Elegance 2	Rating	\N	\N	2	f	\N	2021-04-11 01:09:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
398	\N	Elegance 1	Rating	\N	\N	1	f	\N	2021-04-11 01:08:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
399	\N	Ease of Prep 4	Rating	\N	\N	4	f	\N	2021-04-11 01:07:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
400	\N	Ease of Prep 3	Rating	\N	\N	3	f	\N	2021-04-11 01:06:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
401	\N	Ease of Prep 2	Rating	\N	\N	2	f	\N	2021-04-11 01:05:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
402	\N	Ease of Prep 1	Rating	\N	\N	1	f	\N	2021-04-11 01:04:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
406	\N	chicken breasts	Ingredient	\N	\N	\N	f	\N	2021-04-11 01:00:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
408	dd	notused3	Ingredient	\N	\N	\N	f	\N	2021-04-11 00:58:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
415	\N	cheap 1	Rating	\N	\N	1	f	\N	2021-04-11 00:51:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
416	dd	Quick to Prepare 1	Rating	\N	\N	1.10000000000000009	f	\N	2021-04-11 00:50:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
417	\N	Healthy 4	Rating	\N	\N	4	f	\N	2021-04-11 00:49:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
418	jj	notused2	Ingredient	\N	\N	\N	f	\N	2021-04-11 00:48:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
419	\N	Healthy 3	Rating	\N	\N	3	f	\N	2021-04-11 00:47:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
420	\N	unused	Ingredient	\N	\N	\N	f	\N	2021-04-11 00:46:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
422	\N	Healthy 2	Rating	\N	\N	2	f	\N	2021-04-11 00:44:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
424	\N	Healthy 1	Rating	\N	\N	1	f	\N	2021-04-11 00:42:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
425	\N	Quick To Table 4	Rating	\N	\N	4	f	\N	2021-04-11 00:41:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
426	\N	Quick To Table 3	Rating	\N	\N	3	f	\N	2021-04-11 00:40:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
427	\N	Quick To Table 2	Rating	\N	\N	2	f	\N	2021-04-11 00:39:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
428	\N	Quick To Table 1	Rating	\N	\N	1	f	\N	2021-04-11 00:38:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
432	dd	Side Dish	DishType	\N	\N	\N	f	\N	2021-04-11 00:34:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
433	tt	Dessert	DishType	\N	\N	\N	f	\N	2021-04-11 00:33:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
434	\N	chicken	Ingredient	\N	\N	\N	f	\N	2021-04-11 00:32:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
435	\N	ground beef	Ingredient	\N	\N	\N	f	\N	2021-04-11 00:31:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
436	\N	elbow maccaroni	Ingredient	\N	\N	\N	f	\N	2021-04-11 00:30:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
437	\N	chicken stock	Ingredient	\N	\N	\N	f	\N	2021-04-11 00:29:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
438	\N	farfalla	Ingredient	\N	\N	\N	f	\N	2021-04-11 00:28:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
439	\N	spaghetti	Ingredient	\N	\N	\N	f	\N	2021-04-11 00:27:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
440	\N	pancetta	Ingredient	\N	\N	\N	f	\N	2021-04-11 00:26:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
441	\N	bacon	Ingredient	\N	\N	\N	f	\N	2021-04-11 00:25:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
442	\N	pork chops	Ingredient	\N	\N	\N	f	\N	2021-04-11 00:24:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
443	\N	chicken thighs	Ingredient	\N	\N	\N	f	\N	2021-04-11 00:23:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
444	\N	half and half	Ingredient	\N	\N	\N	f	\N	2021-04-11 00:22:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
445	\N	unused	TagType	\N	\N	\N	f	\N	2021-04-11 00:21:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
446	\N	raisin	Ingredient	\N	\N	\N	f	\N	2021-04-11 00:20:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
447	\N	jasmine rice	Ingredient	\N	\N	\N	f	\N	2021-04-11 00:19:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
448	\N	coconut milk	Ingredient	\N	\N	\N	f	\N	2021-04-11 00:18:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
449	\N	rice wine vinegar	Ingredient	\N	\N	\N	f	\N	2021-04-11 00:17:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
450	\N	fish sauce	Ingredient	\N	\N	\N	f	\N	2021-04-11 00:16:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
451	\N	shitake mushrooms	Ingredient	\N	\N	\N	f	\N	2021-04-11 00:15:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
452	\N	green onion	Ingredient	\N	\N	\N	f	\N	2021-04-11 00:14:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
453	\N	mozzarella	Ingredient	\N	\N	\N	f	\N	2021-04-11 00:13:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
454	\N	canned mixed vegetables	Ingredient	\N	\N	\N	f	\N	2021-04-11 00:12:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
455	\N	bulgur	Ingredient	\N	\N	\N	f	\N	2021-04-11 00:11:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
456	\N	green lentils	Ingredient	\N	\N	\N	f	\N	2021-04-11 00:10:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
457	\N	Worcestershire sauce	Ingredient	\N	\N	\N	f	\N	2021-04-11 00:09:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
458	\N	apple juice	Ingredient	\N	\N	\N	f	\N	2021-04-11 00:08:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
459	\N	canned salmon	Ingredient	\N	\N	\N	f	\N	2021-04-11 00:07:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
460	\N	cornstarch	Ingredient	\N	\N	\N	f	\N	2021-04-11 00:06:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
461	\N	beer	Ingredient	\N	\N	\N	f	\N	2021-04-11 00:05:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
462	\N	plain yogurt	Ingredient	\N	\N	\N	f	\N	2021-04-11 00:04:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
463	\N	lime	Ingredient	\N	\N	\N	f	\N	2021-04-11 00:03:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
464	\N	dried dill	Ingredient	\N	\N	\N	f	\N	2021-04-11 00:02:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
465	\N	cabbage	Ingredient	\N	\N	\N	f	\N	2021-04-11 00:01:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
466	\N	bowtie pasta	Ingredient	\N	\N	\N	f	\N	2021-04-11 00:00:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
467	\N	cherry tomatoes	Ingredient	\N	\N	\N	f	\N	2021-04-10 23:59:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
468	\N	dinner in a bag	Ingredient	\N	\N	\N	f	\N	2021-04-10 23:58:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
469	\N	glue	NonEdible	\N	\N	\N	f	\N	2021-04-10 23:57:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
470	\N	soft cat food	NonEdible	\N	\N	\N	f	\N	2021-04-10 23:56:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
471	\N	pantry dish	TagType	\N	\N	\N	f	\N	2021-04-10 23:55:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
472	\N	sliced pepperoni	Ingredient	\N	\N	\N	f	\N	2021-04-10 23:54:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
473	\N	boneless pork chops	Ingredient	\N	\N	\N	f	\N	2021-04-10 23:53:31.48136+00	\N	\N	\N	f	\N	1	\N	\N	\N
501	\N	tag2	TagType	f	t	0	f	\N	\N	\N	\N	\N	f	\N	1	\N	\N	\N
502	\N	tag3	TagType	f	t	0	f	\N	\N	\N	\N	\N	f	\N	1	\N	\N	\N
503	\N	tag4	TagType	f	t	0	f	\N	\N	\N	\N	\N	f	\N	1	\N	\N	\N
500	\N	tag1	TagType	f	t	0	f	\N	\N	\N	\N	\N	f	\N	1	\N	\N	\N
504	\N	tag5	TagType	f	t	0	f	\N	\N	\N	\N	\N	f	\N	1	\N	\N	\N
505	\N	notdisplayed	Ingredient	f	t	0	f	\N	\N	\N	\N	\N	f	\N	1	\N	\N	\N
506	\N	notdisplayed	Ingredient	f	t	0	t	\N	\N	\N	\N	\N	f	\N	1	\N	\N	\N
1089	\N	c	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:02.318+00	\N	\N	\N	f	\N	1	\N	\N	\N
1092	\N	testTagSibling	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:02.785+00	\N	\N	\N	f	\N	1	\N	\N	\N
1093	\N	testTagSibling2	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:03.158+00	\N	\N	\N	f	\N	1	\N	\N	\N
1094	\N	testTagChild	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:03.52+00	\N	\N	\N	f	\N	1	\N	\N	\N
1095	\N	testTagAnotherChild	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:03.701+00	\N	\N	\N	f	\N	1	\N	\N	\N
1098	\N	c	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:04.618+00	\N	\N	\N	f	\N	1	\N	\N	\N
1101	\N	testTagSibling	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:04.959+00	\N	\N	\N	f	\N	1	\N	\N	\N
1102	\N	testTagSibling2	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:05.341+00	\N	\N	\N	f	\N	1	\N	\N	\N
1103	\N	testTagChild	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:05.723+00	\N	\N	\N	f	\N	1	\N	\N	\N
1104	\N	testTagAnotherChild	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:05.898+00	\N	\N	\N	f	\N	1	\N	\N	\N
1107	\N	c	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:06.703+00	\N	\N	\N	f	\N	1	\N	\N	\N
1110	\N	testTagSibling	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:07.199+00	\N	\N	\N	f	\N	1	\N	\N	\N
1111	\N	testTagSibling2	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:07.531+00	\N	\N	\N	f	\N	1	\N	\N	\N
1112	\N	testTagChild	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:07.903+00	\N	\N	\N	f	\N	1	\N	\N	\N
1113	\N	testTagAnotherChild	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:08.058+00	\N	\N	\N	f	\N	1	\N	\N	\N
1116	\N	c	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:09.115+00	\N	\N	\N	f	\N	1	\N	\N	\N
1119	\N	testTagSibling	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:09.724+00	\N	\N	\N	f	\N	1	\N	\N	\N
1120	\N	testTagSibling2	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:10.121+00	\N	\N	\N	f	\N	1	\N	\N	\N
1121	\N	testTagChild	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:10.519+00	\N	\N	\N	f	\N	1	\N	\N	\N
1122	\N	testTagAnotherChild	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:10.821+00	\N	\N	\N	f	\N	1	\N	\N	\N
1125	\N	c	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:12.017+00	\N	\N	\N	f	\N	1	\N	\N	\N
1126	testdescription	testname	TagType	\N	\N	\N	f	\N	\N	\N	\N	\N	f	\N	1	\N	\N	\N
1129	\N	testTagSibling	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:12.471+00	\N	\N	\N	f	\N	1	\N	\N	\N
1130	\N	testTagSibling2	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:12.887+00	\N	\N	\N	f	\N	1	\N	\N	\N
1131	\N	testTagChild	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:13.296+00	\N	\N	\N	f	\N	1	\N	\N	\N
1132	\N	testTagAnotherChild	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:13.487+00	\N	\N	\N	f	\N	1	\N	\N	\N
1135	\N	c	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:14.526+00	\N	\N	\N	f	\N	1	\N	\N	\N
1138	\N	testTagSibling	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:15.027+00	\N	\N	\N	f	\N	1	\N	\N	\N
1139	\N	testTagSibling2	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:15.564+00	\N	\N	\N	f	\N	1	\N	\N	\N
1140	\N	testTagChild	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:16.078+00	\N	\N	\N	f	\N	1	\N	\N	\N
1141	\N	testTagAnotherChild	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:16.371+00	\N	\N	\N	f	\N	1	\N	\N	\N
1144	\N	c	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:17.735+00	\N	\N	\N	f	\N	1	\N	\N	\N
1147	\N	testTagSibling	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:18.307+00	\N	\N	\N	f	\N	1	\N	\N	\N
1148	\N	testTagSibling2	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:18.858+00	\N	\N	\N	f	\N	1	\N	\N	\N
1149	\N	testTagChild	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:19.421+00	\N	\N	\N	f	\N	1	\N	\N	\N
1150	\N	testTagAnotherChild	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:19.675+00	\N	\N	\N	f	\N	1	\N	\N	\N
1153	\N	c	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:20.826+00	\N	\N	\N	f	\N	1	\N	\N	\N
1157	\N	testTagSibling	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:21.507+00	\N	\N	\N	f	\N	1	\N	\N	\N
1158	\N	testTagSibling2	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:22.054+00	\N	\N	\N	f	\N	1	\N	\N	\N
1159	\N	testTagChild	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:22.5+00	\N	\N	\N	f	\N	1	\N	\N	\N
1160	\N	testTagAnotherChild	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:22.763+00	\N	\N	\N	f	\N	1	\N	\N	\N
1163	\N	c	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:23.845+00	\N	\N	\N	f	\N	1	\N	\N	\N
1166	\N	testTagSibling	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:24.531+00	\N	\N	\N	f	\N	1	\N	\N	\N
1167	\N	testTagSibling2	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:25.163+00	\N	\N	\N	f	\N	1	\N	\N	\N
1168	\N	testTagChild	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:25.664+00	\N	\N	\N	f	\N	1	\N	\N	\N
1169	\N	testTagAnotherChild	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:25.887+00	\N	\N	\N	f	\N	1	\N	\N	\N
1172	\N	c	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:27.071+00	\N	\N	\N	f	\N	1	\N	\N	\N
1175	\N	testTagSibling	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:27.768+00	\N	\N	\N	f	\N	1	\N	\N	\N
1176	\N	testTagSibling2	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:28.241+00	\N	\N	\N	f	\N	1	\N	\N	\N
1177	\N	testTagChild	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:28.839+00	\N	\N	\N	f	\N	1	\N	\N	\N
1178	\N	testTagAnotherChild	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:29.106+00	\N	\N	\N	f	\N	1	\N	\N	\N
1181	\N	c	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:30.402+00	\N	\N	\N	f	\N	1	\N	\N	\N
1184	\N	testTagSibling	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:31.012+00	\N	\N	\N	f	\N	1	\N	\N	\N
1185	\N	testTagSibling2	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:31.628+00	\N	\N	\N	f	\N	1	\N	\N	\N
1186	\N	testTagChild	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:32.226+00	\N	\N	\N	f	\N	1	\N	\N	\N
1187	\N	testTagAnotherChild	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:32.491+00	\N	\N	\N	f	\N	1	\N	\N	\N
1190	\N	c	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:33.896+00	\N	\N	\N	f	\N	1	\N	\N	\N
1002	\N	testTagSibling	TagType	\N	\N	\N	f	\N	2021-04-11 07:46:44.741+00	\N	\N	\N	f	\N	1	\N	\N	\N
1003	\N	testTagSibling2	TagType	\N	\N	\N	f	\N	2021-04-11 07:46:44.949+00	\N	\N	\N	f	\N	1	\N	\N	\N
1004	\N	testTagChild	TagType	\N	\N	\N	f	\N	2021-04-11 07:46:45.086+00	\N	\N	\N	f	\N	1	\N	\N	\N
1005	\N	testTagAnotherChild	TagType	\N	\N	\N	f	\N	2021-04-11 07:46:45.161+00	\N	\N	\N	f	\N	1	\N	\N	\N
1008	\N	c	TagType	\N	\N	\N	f	\N	2021-04-11 07:46:45.575+00	\N	\N	\N	f	\N	1	\N	\N	\N
1011	\N	testTagSibling	TagType	\N	\N	\N	f	\N	2021-04-11 07:46:45.996+00	\N	\N	\N	f	\N	1	\N	\N	\N
1012	\N	testTagSibling2	TagType	\N	\N	\N	f	\N	2021-04-11 07:46:46.157+00	\N	\N	\N	f	\N	1	\N	\N	\N
1013	\N	testTagChild	TagType	\N	\N	\N	f	\N	2021-04-11 07:46:46.311+00	\N	\N	\N	f	\N	1	\N	\N	\N
1014	\N	testTagAnotherChild	TagType	\N	\N	\N	f	\N	2021-04-11 07:46:46.398+00	\N	\N	\N	f	\N	1	\N	\N	\N
1017	\N	c	TagType	\N	\N	\N	f	\N	2021-04-11 07:46:46.871+00	\N	\N	\N	f	\N	1	\N	\N	\N
1020	\N	testTagSibling	TagType	\N	\N	\N	f	\N	2021-04-11 07:46:47.685+00	\N	\N	\N	f	\N	1	\N	\N	\N
1021	\N	testTagSibling2	TagType	\N	\N	\N	f	\N	2021-04-11 07:46:47.914+00	\N	\N	\N	f	\N	1	\N	\N	\N
1022	\N	testTagChild	TagType	\N	\N	\N	f	\N	2021-04-11 07:46:48.198+00	\N	\N	\N	f	\N	1	\N	\N	\N
1023	\N	testTagAnotherChild	TagType	\N	\N	\N	f	\N	2021-04-11 07:46:48.339+00	\N	\N	\N	f	\N	1	\N	\N	\N
1026	\N	c	TagType	\N	\N	\N	f	\N	2021-04-11 07:46:48.929+00	\N	\N	\N	f	\N	1	\N	\N	\N
1029	\N	testTagSibling	TagType	\N	\N	\N	f	\N	2021-04-11 07:46:49.826+00	\N	\N	\N	f	\N	1	\N	\N	\N
1030	\N	testTagSibling2	TagType	\N	\N	\N	f	\N	2021-04-11 07:46:50.073+00	\N	\N	\N	f	\N	1	\N	\N	\N
1031	\N	testTagChild	TagType	\N	\N	\N	f	\N	2021-04-11 07:46:50.317+00	\N	\N	\N	f	\N	1	\N	\N	\N
1032	\N	testTagAnotherChild	TagType	\N	\N	\N	f	\N	2021-04-11 07:46:50.432+00	\N	\N	\N	f	\N	1	\N	\N	\N
1035	\N	c	TagType	\N	\N	\N	f	\N	2021-04-11 07:46:51.093+00	\N	\N	\N	f	\N	1	\N	\N	\N
1038	\N	testTagSibling	TagType	\N	\N	\N	f	\N	2021-04-11 07:46:51.471+00	\N	\N	\N	f	\N	1	\N	\N	\N
1039	\N	testTagSibling2	TagType	\N	\N	\N	f	\N	2021-04-11 07:46:51.708+00	\N	\N	\N	f	\N	1	\N	\N	\N
1040	\N	testTagChild	TagType	\N	\N	\N	f	\N	2021-04-11 07:46:51.916+00	\N	\N	\N	f	\N	1	\N	\N	\N
1041	\N	testTagAnotherChild	TagType	\N	\N	\N	f	\N	2021-04-11 07:46:52.028+00	\N	\N	\N	f	\N	1	\N	\N	\N
1044	\N	c	TagType	\N	\N	\N	f	\N	2021-04-11 07:46:52.666+00	\N	\N	\N	f	\N	1	\N	\N	\N
1047	\N	testTagSibling	TagType	\N	\N	\N	f	\N	2021-04-11 07:46:52.942+00	\N	\N	\N	f	\N	1	\N	\N	\N
1048	\N	testTagSibling2	TagType	\N	\N	\N	f	\N	2021-04-11 07:46:53.173+00	\N	\N	\N	f	\N	1	\N	\N	\N
1049	\N	testTagChild	TagType	\N	\N	\N	f	\N	2021-04-11 07:46:53.407+00	\N	\N	\N	f	\N	1	\N	\N	\N
1050	\N	testTagAnotherChild	TagType	\N	\N	\N	f	\N	2021-04-11 07:46:53.532+00	\N	\N	\N	f	\N	1	\N	\N	\N
1053	\N	c	TagType	\N	\N	\N	f	\N	2021-04-11 07:46:54.226+00	\N	\N	\N	f	\N	1	\N	\N	\N
1056	\N	testTagSibling	TagType	\N	\N	\N	f	\N	2021-04-11 07:46:54.561+00	\N	\N	\N	f	\N	1	\N	\N	\N
1057	\N	testTagSibling2	TagType	\N	\N	\N	f	\N	2021-04-11 07:46:54.918+00	\N	\N	\N	f	\N	1	\N	\N	\N
1058	\N	testTagChild	TagType	\N	\N	\N	f	\N	2021-04-11 07:46:55.204+00	\N	\N	\N	f	\N	1	\N	\N	\N
1059	\N	testTagAnotherChild	TagType	\N	\N	\N	f	\N	2021-04-11 07:46:55.333+00	\N	\N	\N	f	\N	1	\N	\N	\N
1062	\N	c	TagType	\N	\N	\N	f	\N	2021-04-11 07:46:56.031+00	\N	\N	\N	f	\N	1	\N	\N	\N
1065	\N	testTagSibling	TagType	\N	\N	\N	f	\N	2021-04-11 07:46:56.366+00	\N	\N	\N	f	\N	1	\N	\N	\N
1066	\N	testTagSibling2	TagType	\N	\N	\N	f	\N	2021-04-11 07:46:56.636+00	\N	\N	\N	f	\N	1	\N	\N	\N
1067	\N	testTagChild	TagType	\N	\N	\N	f	\N	2021-04-11 07:46:57.037+00	\N	\N	\N	f	\N	1	\N	\N	\N
1068	\N	testTagAnotherChild	TagType	\N	\N	\N	f	\N	2021-04-11 07:46:57.199+00	\N	\N	\N	f	\N	1	\N	\N	\N
1071	\N	c	TagType	\N	\N	\N	f	\N	2021-04-11 07:46:57.992+00	\N	\N	\N	f	\N	1	\N	\N	\N
1074	\N	testTagSibling	TagType	\N	\N	\N	f	\N	2021-04-11 07:46:58.342+00	\N	\N	\N	f	\N	1	\N	\N	\N
1075	\N	testTagSibling2	TagType	\N	\N	\N	f	\N	2021-04-11 07:46:58.717+00	\N	\N	\N	f	\N	1	\N	\N	\N
1076	\N	testTagChild	TagType	\N	\N	\N	f	\N	2021-04-11 07:46:59.001+00	\N	\N	\N	f	\N	1	\N	\N	\N
1077	\N	testTagAnotherChild	TagType	\N	\N	\N	f	\N	2021-04-11 07:46:59.141+00	\N	\N	\N	f	\N	1	\N	\N	\N
1080	\N	c	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:00.1+00	\N	\N	\N	f	\N	1	\N	\N	\N
1083	\N	testTagSibling	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:00.485+00	\N	\N	\N	f	\N	1	\N	\N	\N
1084	\N	testTagSibling2	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:00.845+00	\N	\N	\N	f	\N	1	\N	\N	\N
1085	\N	testTagChild	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:01.214+00	\N	\N	\N	f	\N	1	\N	\N	\N
50532	\N	cream cheese	TagType	\N	\N	\N	f	\N	2022-04-11 07:47:01.214+00	\N	\N	\N	f	\N	1	\N	\N	\N
1086	\N	testTagAnotherChild	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:01.408+00	\N	\N	\N	f	\N	1	\N	\N	\N
393	\N	Milk and Cream	Ingredient	\N	\N	\N	f	\N	2021-04-11 01:13:31.48136+00	\N	\N	\N	t	\N	1	\N	\N	\N
394	\N	sausage	Ingredient	\N	\N	\N	f	\N	2021-04-11 01:12:31.48136+00	\N	\N	\N	t	\N	1	\N	\N	\N
292	\N	Elegance	Rating	\N	\N	\N	f	\N	2021-04-11 02:54:31.48136+00	\N	\N	\N	t	\N	1	\N	\N	\N
291	\N	Ease of Prep	Rating	\N	\N	\N	f	\N	2021-04-11 02:55:31.48136+00	\N	\N	\N	t	\N	1	\N	\N	\N
407	\N	Dish Type	TagType	\N	\N	\N	f	\N	2021-04-11 00:59:31.48136+00	\N	\N	\N	t	\N	1	\N	\N	\N
410	\N	Nuts	Ingredient	\N	\N	\N	f	\N	2021-04-11 00:56:31.48136+00	\N	\N	\N	t	\N	1	\N	\N	\N
423	\N	Nuts, Grains and Dry Beans	Ingredient	\N	\N	\N	f	\N	2021-04-11 00:43:31.48136+00	\N	\N	\N	t	\N	1	\N	\N	\N
429	\N	Meat Type	TagType	\N	\N	\N	f	\N	2021-04-11 00:37:31.48136+00	\N	\N	\N	t	\N	1	\N	\N	\N
368	\N	Chicken	Ingredient	\N	\N	\N	f	\N	2021-04-11 01:38:31.48136+00	\N	\N	\N	t	\N	1	\N	\N	\N
372	\N	Beef	Ingredient	\N	\N	\N	f	\N	2021-04-11 01:34:31.48136+00	\N	\N	\N	t	\N	1	\N	\N	\N
376	\N	pasta	Ingredient	\N	\N	\N	f	\N	2021-04-11 01:30:31.48136+00	\N	\N	\N	t	\N	1	\N	\N	\N
375	\N	Pork	Ingredient	\N	\N	\N	f	\N	2021-04-11 01:31:31.48136+00	\N	\N	\N	t	\N	1	\N	\N	\N
378	\N	Eggs and Dairy	Ingredient	\N	\N	\N	f	\N	2021-04-11 01:28:31.48136+00	\N	\N	\N	t	\N	1	\N	\N	\N
369	\N	New	TagType	t	\N	\N	f	\N	2021-04-11 01:37:31.48136+00	\N	\N	\N	t	\N	1	\N	\N	\N
14	\N	Rice	Ingredient	\N	\N	\N	f	\N	2021-04-11 07:32:31.48136+00	\N	\N	\N	t	\N	1	\N	\N	\N
370	\N	New	Ingredient	t	\N	\N	f	\N	2021-04-11 01:36:31.48136+00	\N	\N	\N	t	\N	1	\N	\N	\N
379	\N	Condiments	Ingredient	\N	\N	\N	f	\N	2021-04-11 01:27:31.48136+00	\N	\N	\N	t	\N	1	\N	\N	\N
1016	\N	b	TagType	\N	\N	\N	f	\N	2021-04-11 07:46:46.757+00	\N	\N	\N	t	\N	1	\N	\N	\N
390	\N	Vegetables	Ingredient	\N	\N	\N	f	\N	2021-04-11 01:16:31.48136+00	\N	\N	\N	t	\N	1	\N	\N	\N
381	\N	Cheese	Ingredient	\N	\N	\N	f	\N	2021-04-11 01:25:31.48136+00	\N	\N	\N	t	\N	1	\N	\N	\N
386	\N	Canned Vegetables	Ingredient	\N	\N	\N	f	\N	2021-04-11 01:20:31.48136+00	\N	\N	\N	t	\N	1	\N	\N	\N
392	\N	Drinks	Ingredient	\N	\N	\N	f	\N	2021-04-11 01:14:31.48136+00	\N	\N	\N	t	\N	1	\N	\N	\N
431	\N	Canned Fish	Ingredient	\N	\N	\N	f	\N	2021-04-11 00:35:31.48136+00	\N	\N	\N	t	\N	1	\N	\N	\N
382	\N	Baking	Ingredient	\N	\N	\N	f	\N	2021-04-11 01:24:31.48136+00	\N	\N	\N	t	\N	1	\N	\N	\N
388	\N	Produce	Ingredient	\N	\N	\N	f	\N	2021-04-11 01:18:31.48136+00	\N	\N	\N	t	\N	1	\N	\N	\N
377	\N	Spices	Ingredient	\N	\N	\N	f	\N	2021-04-11 01:29:31.48136+00	\N	\N	\N	t	\N	1	\N	\N	\N
403	\N	Frozen	Ingredient	\N	\N	\N	f	\N	2021-04-11 01:03:31.48136+00	\N	\N	\N	t	\N	1	\N	\N	\N
384	\N	Office Supplies	NonEdible	\N	\N	\N	f	\N	2021-04-11 01:22:31.48136+00	\N	\N	\N	t	\N	1	\N	\N	\N
367	\N	Animal Products	NonEdible	\N	\N	\N	f	\N	2021-04-11 01:39:31.48136+00	\N	\N	\N	t	\N	1	\N	\N	\N
421	\N	lunch meats	Ingredient	\N	\N	\N	f	\N	2021-04-11 00:45:31.48136+00	\N	\N	\N	t	\N	1	\N	\N	\N
9	\N	Mutton	Ingredient	\N	\N	\N	f	\N	2021-04-11 07:37:31.48136+00	\N	\N	\N	t	\N	1	\N	\N	\N
35	\N	Cultural Roots	TagType	\N	\N	\N	f	\N	2021-04-11 07:11:31.48136+00	\N	\N	\N	t	\N	1	\N	\N	\N
88	\N	Prepared Meats	Ingredient	\N	\N	\N	f	\N	2021-04-11 06:18:31.48136+00	\N	\N	\N	t	\N	1	\N	\N	\N
157	\N	Preparation Type	TagType	\N	\N	\N	f	\N	2021-04-11 05:09:31.48136+00	\N	\N	\N	t	\N	1	\N	\N	\N
216	\N	Kitchen Supplies	NonEdible	\N	\N	\N	f	\N	2021-04-11 04:10:31.48136+00	\N	\N	\N	t	\N	1	\N	\N	\N
246	\N	Special Diet	TagType	\N	\N	\N	f	\N	2021-04-11 03:40:31.48136+00	\N	\N	\N	t	\N	1	\N	\N	\N
313	\N	Household Supplies	NonEdible	\N	\N	\N	f	\N	2021-04-11 02:33:31.48136+00	\N	\N	\N	t	\N	1	\N	\N	\N
371	mmm	Meat	Ingredient	\N	\N	\N	f	\N	2021-04-11 01:35:31.48136+00	\N	\N	\N	t	\N	1	\N	\N	\N
373	\N	Fish	Ingredient	\N	\N	\N	f	\N	2021-04-11 01:33:31.48136+00	\N	\N	\N	t	\N	1	\N	\N	\N
374	\N	Poultry	Ingredient	\N	\N	\N	f	\N	2021-04-11 01:32:31.48136+00	\N	\N	\N	t	\N	1	\N	\N	\N
380	\N	New	NonEdible	t	\N	\N	f	\N	2021-04-11 01:26:31.48136+00	\N	\N	\N	t	\N	1	\N	\N	\N
383	\N	cleaning supplies	NonEdible	\N	\N	\N	f	\N	2021-04-11 01:23:31.48136+00	\N	\N	\N	t	\N	1	\N	\N	\N
385	\N	Oil and Vinegar	Ingredient	\N	\N	\N	f	\N	2021-04-11 01:21:31.48136+00	\N	\N	\N	t	\N	1	\N	\N	\N
389	\N	Personal Hygiene	NonEdible	\N	\N	\N	f	\N	2021-04-11 01:17:31.48136+00	\N	\N	\N	t	\N	1	\N	\N	\N
391	\N	Taste Factor	Rating	\N	\N	\N	f	\N	2021-04-11 01:15:31.48136+00	\N	\N	\N	t	\N	1	\N	\N	\N
404	\N	Holiday	TagType	\N	\N	\N	f	\N	2021-04-11 01:02:31.48136+00	\N	\N	\N	t	\N	1	\N	\N	\N
405	\N	Prepared Soup	Ingredient	\N	\N	\N	f	\N	2021-04-11 01:01:31.48136+00	\N	\N	\N	t	\N	1	\N	\N	\N
409	\N	Bakery - Bread Products	Ingredient	\N	\N	\N	f	\N	2021-04-11 00:57:31.48136+00	\N	\N	\N	t	\N	1	\N	\N	\N
411	\N	Spreads	Ingredient	\N	\N	\N	f	\N	2021-04-11 00:55:31.48136+00	\N	\N	\N	t	\N	1	\N	\N	\N
412	\N	Tomato and Pasta Sauce	Ingredient	\N	\N	\N	f	\N	2021-04-11 00:54:31.48136+00	\N	\N	\N	t	\N	1	\N	\N	\N
413	\N	Snacks	Ingredient	\N	\N	\N	f	\N	2021-04-11 00:53:31.48136+00	\N	\N	\N	t	\N	1	\N	\N	\N
414	\N	Coffee and Tea	Ingredient	\N	\N	\N	f	\N	2021-04-11 00:52:31.48136+00	\N	\N	\N	t	\N	1	\N	\N	\N
160	\N	Quick To Prepare	Rating	\N	\N	\N	f	\N	2021-04-11 05:06:31.48136+00	\N	\N	\N	t	\N	1	\N	\N	\N
305	\N	Healthy	Rating	\N	\N	\N	f	\N	2021-04-11 02:41:31.48136+00	\N	\N	\N	t	\N	1	\N	\N	\N
306	\N	Kids Like It	Rating	\N	\N	\N	f	\N	2021-04-11 02:40:31.48136+00	\N	\N	\N	t	\N	1	\N	\N	\N
290	\N	Quick To Table	Rating	\N	\N	\N	f	\N	2021-04-11 02:56:31.48136+00	\N	\N	\N	t	\N	1	\N	\N	\N
332	\N	cheap	Rating	t	\N	\N	f	\N	2021-04-11 02:14:31.48136+00	\N	\N	\N	t	\N	1	\N	\N	\N
87	\N	Occasions	TagType	\N	\N	\N	f	\N	2021-04-11 06:19:31.48136+00	\N	\N	\N	t	\N	1	\N	\N	\N
430	\N	Cereals	Ingredient	\N	\N	\N	f	\N	2021-04-11 00:36:31.48136+00	\N	\N	\N	t	\N	1	\N	\N	\N
197	\N	Low In ...	TagType	\N	\N	\N	f	\N	2021-04-11 04:29:31.48136+00	\N	\N	\N	t	\N	1	\N	\N	\N
1000	main1	parent	TagType	\N	\N	\N	f	\N	\N	\N	\N	\N	t	\N	1	\N	\N	\N
1001	\N	testTag	TagType	\N	\N	\N	f	\N	2021-04-11 07:46:44.528+00	\N	\N	\N	t	\N	1	\N	\N	\N
1006	\N	a	TagType	\N	\N	\N	f	\N	2021-04-11 07:46:45.308+00	\N	2021-04-11 07:46:45.444+00	\N	t	\N	1	\N	\N	\N
1007	\N	b	TagType	\N	\N	\N	f	\N	2021-04-11 07:46:45.48+00	\N	\N	\N	t	\N	1	\N	\N	\N
1009	main1	parent	TagType	\N	\N	\N	f	\N	\N	\N	\N	\N	t	\N	1	\N	\N	\N
1010	\N	testTag	TagType	\N	\N	\N	f	\N	2021-04-11 07:46:45.918+00	\N	\N	\N	t	\N	1	\N	\N	\N
1015	\N	a	TagType	\N	\N	\N	f	\N	2021-04-11 07:46:46.564+00	\N	2021-04-11 07:46:46.721+00	\N	t	\N	1	\N	\N	\N
1018	main1	parent	TagType	\N	\N	\N	f	\N	\N	\N	\N	\N	t	\N	1	\N	\N	\N
1019	\N	testTag	TagType	\N	\N	\N	f	\N	2021-04-11 07:46:47.562+00	\N	\N	\N	t	\N	1	\N	\N	\N
1024	\N	a	TagType	\N	\N	\N	f	\N	2021-04-11 07:46:48.572+00	\N	2021-04-11 07:46:48.744+00	\N	t	\N	1	\N	\N	\N
1025	\N	b	TagType	\N	\N	\N	f	\N	2021-04-11 07:46:48.795+00	\N	\N	\N	t	\N	1	\N	\N	\N
1027	main1	parent	TagType	\N	\N	\N	f	\N	\N	\N	\N	\N	t	\N	1	\N	\N	\N
1028	\N	testTag	TagType	\N	\N	\N	f	\N	2021-04-11 07:46:49.722+00	\N	\N	\N	t	\N	1	\N	\N	\N
1033	\N	a	TagType	\N	\N	\N	f	\N	2021-04-11 07:46:50.696+00	\N	2021-04-11 07:46:50.898+00	\N	t	\N	1	\N	\N	\N
1034	\N	b	TagType	\N	\N	\N	f	\N	2021-04-11 07:46:50.956+00	\N	\N	\N	t	\N	1	\N	\N	\N
1036	main1	parent	TagType	\N	\N	\N	f	\N	\N	\N	\N	\N	t	\N	1	\N	\N	\N
1037	\N	testTag	TagType	\N	\N	\N	f	\N	2021-04-11 07:46:51.36+00	\N	\N	\N	t	\N	1	\N	\N	\N
1042	\N	a	TagType	\N	\N	\N	f	\N	2021-04-11 07:46:52.312+00	\N	2021-04-11 07:46:52.496+00	\N	t	\N	1	\N	\N	\N
1043	\N	b	TagType	\N	\N	\N	f	\N	2021-04-11 07:46:52.526+00	\N	\N	\N	t	\N	1	\N	\N	\N
1045	main1	parent	TagType	\N	\N	\N	f	\N	\N	\N	\N	\N	t	\N	1	\N	\N	\N
1046	\N	testTag	TagType	\N	\N	\N	f	\N	2021-04-11 07:46:52.818+00	\N	\N	\N	t	\N	1	\N	\N	\N
1051	\N	a	TagType	\N	\N	\N	f	\N	2021-04-11 07:46:53.832+00	\N	2021-04-11 07:46:54.047+00	\N	t	\N	1	\N	\N	\N
1052	\N	b	TagType	\N	\N	\N	f	\N	2021-04-11 07:46:54.075+00	\N	\N	\N	t	\N	1	\N	\N	\N
1054	main1	parent	TagType	\N	\N	\N	f	\N	\N	\N	\N	\N	t	\N	1	\N	\N	\N
1055	\N	testTag	TagType	\N	\N	\N	f	\N	2021-04-11 07:46:54.422+00	\N	\N	\N	t	\N	1	\N	\N	\N
1060	\N	a	TagType	\N	\N	\N	f	\N	2021-04-11 07:46:55.637+00	\N	2021-04-11 07:46:55.863+00	\N	t	\N	1	\N	\N	\N
1061	\N	b	TagType	\N	\N	\N	f	\N	2021-04-11 07:46:55.886+00	\N	\N	\N	t	\N	1	\N	\N	\N
1063	main1	parent	TagType	\N	\N	\N	f	\N	\N	\N	\N	\N	t	\N	1	\N	\N	\N
1064	\N	testTag	TagType	\N	\N	\N	f	\N	2021-04-11 07:46:56.213+00	\N	\N	\N	t	\N	1	\N	\N	\N
1069	\N	a	TagType	\N	\N	\N	f	\N	2021-04-11 07:46:57.521+00	\N	2021-04-11 07:46:57.788+00	\N	t	\N	1	\N	\N	\N
1070	\N	b	TagType	\N	\N	\N	f	\N	2021-04-11 07:46:57.812+00	\N	\N	\N	t	\N	1	\N	\N	\N
1072	main1	parent	TagType	\N	\N	\N	f	\N	\N	\N	\N	\N	t	\N	1	\N	\N	\N
1073	\N	testTag	TagType	\N	\N	\N	f	\N	2021-04-11 07:46:58.175+00	\N	\N	\N	t	\N	1	\N	\N	\N
1078	\N	a	TagType	\N	\N	\N	f	\N	2021-04-11 07:46:59.484+00	\N	2021-04-11 07:46:59.812+00	\N	t	\N	1	\N	\N	\N
1079	\N	b	TagType	\N	\N	\N	f	\N	2021-04-11 07:46:59.873+00	\N	\N	\N	t	\N	1	\N	\N	\N
1081	main1	parent	TagType	\N	\N	\N	f	\N	\N	\N	\N	\N	t	\N	1	\N	\N	\N
1082	\N	testTag	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:00.31+00	\N	\N	\N	t	\N	1	\N	\N	\N
1087	\N	a	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:01.852+00	\N	2021-04-11 07:47:02.114+00	\N	t	\N	1	\N	\N	\N
1088	\N	b	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:02.143+00	\N	\N	\N	t	\N	1	\N	\N	\N
1090	main1	parent	TagType	\N	\N	\N	f	\N	\N	\N	\N	\N	t	\N	1	\N	\N	\N
1091	\N	testTag	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:02.589+00	\N	\N	\N	t	\N	1	\N	\N	\N
1096	\N	a	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:04.124+00	\N	2021-04-11 07:47:04.392+00	\N	t	\N	1	\N	\N	\N
1097	\N	b	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:04.412+00	\N	\N	\N	t	\N	1	\N	\N	\N
1099	main1	parent	TagType	\N	\N	\N	f	\N	\N	\N	\N	\N	t	\N	1	\N	\N	\N
1100	\N	testTag	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:04.805+00	\N	\N	\N	t	\N	1	\N	\N	\N
1105	\N	a	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:06.228+00	\N	2021-04-11 07:47:06.516+00	\N	t	\N	1	\N	\N	\N
1106	\N	b	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:06.534+00	\N	\N	\N	t	\N	1	\N	\N	\N
1108	main1	parent	TagType	\N	\N	\N	f	\N	\N	\N	\N	\N	t	\N	1	\N	\N	\N
1109	\N	testTag	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:06.952+00	\N	\N	\N	t	\N	1	\N	\N	\N
1114	\N	a	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:08.497+00	\N	2021-04-11 07:47:08.852+00	\N	t	\N	1	\N	\N	\N
1115	\N	b	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:08.881+00	\N	\N	\N	t	\N	1	\N	\N	\N
1117	main1	parent	TagType	\N	\N	\N	f	\N	\N	\N	\N	\N	t	\N	1	\N	\N	\N
1118	\N	testTag	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:09.453+00	\N	\N	\N	t	\N	1	\N	\N	\N
1123	\N	a	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:11.394+00	\N	2021-04-11 07:47:11.771+00	\N	t	\N	1	\N	\N	\N
1124	\N	b	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:11.804+00	\N	\N	\N	t	\N	1	\N	\N	\N
1127	main1	parent	TagType	\N	\N	\N	f	\N	\N	\N	\N	\N	t	\N	1	\N	\N	\N
1128	\N	testTag	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:12.271+00	\N	\N	\N	t	\N	1	\N	\N	\N
1133	\N	a	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:13.888+00	\N	2021-04-11 07:47:14.238+00	\N	t	\N	1	\N	\N	\N
1134	\N	b	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:14.262+00	\N	\N	\N	t	\N	1	\N	\N	\N
1136	main1	parent	TagType	\N	\N	\N	f	\N	\N	\N	\N	\N	t	\N	1	\N	\N	\N
1137	\N	testTag	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:14.755+00	\N	\N	\N	t	\N	1	\N	\N	\N
1142	\N	a	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:16.976+00	\N	2021-04-11 07:47:17.424+00	\N	t	\N	1	\N	\N	\N
1143	\N	b	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:17.462+00	\N	\N	\N	t	\N	1	\N	\N	\N
1145	main1	parent	TagType	\N	\N	\N	f	\N	\N	\N	\N	\N	t	\N	1	\N	\N	\N
1146	\N	testTag	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:18.03+00	\N	\N	\N	t	\N	1	\N	\N	\N
1151	\N	a	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:20.227+00	\N	2021-04-11 07:47:20.606+00	\N	t	\N	1	\N	\N	\N
1152	\N	b	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:20.626+00	\N	\N	\N	t	\N	1	\N	\N	\N
43	be be be be begas	rutaruta	Ingredient	\N	\N	\N	f	\N	2021-04-11 07:03:31.48136+00	2021-04-11 07:46:47.518+00	\N	\N	t	\N	1	\N	\N	\N
1155	main1	parent	TagType	\N	\N	\N	f	\N	\N	\N	\N	\N	t	\N	1	\N	\N	\N
1156	\N	testTag	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:21.289+00	\N	\N	\N	t	\N	1	\N	\N	\N
1161	\N	a	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:23.232+00	\N	2021-04-11 07:47:23.604+00	\N	t	\N	1	\N	\N	\N
1162	\N	b	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:23.625+00	\N	\N	\N	t	\N	1	\N	\N	\N
1164	main1	parent	TagType	\N	\N	\N	f	\N	\N	\N	\N	\N	t	\N	1	\N	\N	\N
1165	\N	testTag	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:24.231+00	\N	\N	\N	t	\N	1	\N	\N	\N
1170	\N	a	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:26.407+00	\N	2021-04-11 07:47:26.821+00	\N	t	\N	1	\N	\N	\N
1171	\N	b	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:26.842+00	\N	\N	\N	t	\N	1	\N	\N	\N
1173	main1	parent	TagType	\N	\N	\N	f	\N	\N	\N	\N	\N	t	\N	1	\N	\N	\N
1174	\N	testTag	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:27.544+00	\N	\N	\N	t	\N	1	\N	\N	\N
1179	\N	a	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:29.731+00	\N	2021-04-11 07:47:30.134+00	\N	t	\N	1	\N	\N	\N
1180	\N	b	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:30.164+00	\N	\N	\N	t	\N	1	\N	\N	\N
1182	main1	parent	TagType	\N	\N	\N	f	\N	\N	\N	\N	\N	t	\N	1	\N	\N	\N
1183	\N	testTag	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:30.735+00	\N	\N	\N	t	\N	1	\N	\N	\N
1188	\N	a	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:33.174+00	\N	2021-04-11 07:47:33.613+00	\N	t	\N	1	\N	\N	\N
1189	\N	b	TagType	\N	\N	\N	f	\N	2021-04-11 07:47:33.647+00	\N	\N	\N	t	\N	1	\N	\N	\N
\.


--
-- Data for Name: category_tags; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.category_tags (category_id, tag_id) FROM stdin;
9	469
9	470
5	472
5	473
8	1
9	3
7	4
9	5
5	6
9	10
8	12
8	13
6	15
6	16
8	17
8	24
7	18
6	19
7	20
6	21
8	22
8	23
8	25
8	26
5	27
5	28
8	29
5	30
5	31
6	32
6	33
6	34
7	36
7	37
5	38
9	39
9	40
8	41
8	42
6	43
8	44
6	45
5	46
8	47
6	48
8	49
8	50
7	51
8	52
6	53
8	54
6	55
8	78
8	79
8	80
6	81
8	82
9	83
8	84
8	85
5	89
10	90
8	91
8	92
9	93
9	94
8	95
8	96
6	97
6	98
8	99
6	100
6	101
8	102
6	103
6	104
8	105
7	106
6	107
6	108
6	109
8	110
6	111
8	112
8	113
8	114
5	115
6	116
8	117
6	118
6	119
6	120
8	121
7	122
8	123
5	124
7	125
7	126
8	127
8	128
8	129
8	130
7	131
8	132
8	133
8	134
6	135
5	136
8	138
8	139
8	140
9	141
9	144
9	145
7	146
8	147
9	148
9	149
9	150
6	151
9	158
5	159
8	161
5	162
7	163
8	164
8	165
8	166
6	167
8	168
8	169
7	170
8	171
8	172
8	173
8	174
8	175
8	176
8	177
8	178
8	179
9	180
8	181
8	182
8	183
8	184
8	185
8	186
6	187
8	188
8	189
8	190
6	191
8	192
8	193
8	194
9	195
8	196
8	198
6	200
7	201
7	202
7	203
8	205
8	206
6	207
6	208
8	209
8	210
6	211
6	212
8	213
9	214
9	215
8	217
8	220
8	221
9	222
9	223
9	224
9	225
9	226
6	227
5	228
8	229
5	230
8	231
8	232
5	233
8	234
9	235
9	236
8	237
8	238
8	239
8	240
8	241
8	242
8	243
8	244
10	245
6	247
7	248
6	249
8	250
5	251
8	252
8	253
9	254
8	255
8	256
5	257
5	258
8	259
8	260
8	261
6	262
6	263
8	264
9	265
9	266
5	269
9	270
8	271
8	272
8	273
8	274
9	275
9	276
7	277
9	278
7	279
8	282
9	283
6	284
8	285
8	286
6	287
6	288
6	289
8	293
8	294
8	295
8	296
8	297
9	298
9	299
8	308
8	309
6	310
5	311
8	312
8	314
8	318
8	334
8	335
8	336
7	337
8	338
8	339
5	340
8	341
6	342
8	343
7	345
8	347
7	348
6	349
8	350
8	351
8	352
8	353
6	354
6	56
8	57
6	58
6	59
6	60
8	61
8	62
9	63
6	65
8	66
7	67
7	68
7	69
7	70
7	71
8	72
5	73
5	74
6	75
6	76
8	77
6	355
8	356
6	357
8	358
8	359
8	360
8	361
5	406
9	408
9	418
5	420
5	434
5	435
8	436
8	437
8	438
8	439
5	440
6	441
5	442
5	443
7	444
6	446
8	447
8	448
8	449
8	450
6	451
6	452
7	453
8	454
8	455
6	456
8	457
8	458
8	459
8	460
8	461
7	462
6	463
8	464
6	465
8	466
6	467
10	468
6	500
6	501
6	502
6	503
6	504
6	505
1041	1087
1041	1088
1041	1089
1041	1091
1041	1092
1041	1093
1041	1094
1041	1095
1041	1096
1041	1097
1041	1098
1041	1100
1041	1101
1041	1102
1041	1103
1041	1104
1041	1105
1041	1106
1041	1107
1041	1109
1041	1110
1041	1111
1041	1112
1041	1113
1041	1114
1041	1115
1041	1116
1041	1118
1041	1119
1041	1120
1041	1121
1041	1122
1041	1123
1041	1124
1041	1125
1041	1128
1041	1129
1041	1130
1041	1131
1041	1132
1041	1133
1041	1134
1041	1135
1041	1137
1041	1138
1041	1139
1041	1140
1041	1141
1041	1142
1041	1143
1041	1144
1041	1146
1041	1147
1041	1148
1041	1149
1041	1150
1041	1151
1041	1152
1041	1153
1041	1154
1041	1156
1041	1157
1041	1158
1041	1159
1041	1160
1041	1161
1041	1162
1041	1163
1041	1165
1041	1166
1041	1167
1041	1168
1041	1169
1041	1170
1041	1171
1041	1172
1041	1174
1041	1175
1041	1176
1041	1177
1041	1178
1041	1179
1041	1180
1041	1181
1041	1183
1041	1184
1041	1185
1041	1186
1041	1187
1041	1188
1041	1189
1041	1001
1041	1002
1041	1003
1041	1004
1041	1005
1041	1006
1041	1007
1041	1008
1041	1010
1041	1011
1041	1012
1041	1013
1041	1014
1041	1015
1041	1016
1041	1017
1041	1019
1041	1020
1041	1021
1041	1022
1041	1023
1041	1024
1041	1025
1041	1026
1041	1028
1041	1029
1041	1030
1041	1031
1041	1032
1041	1033
1041	1034
1041	1035
1041	1037
1041	1038
1041	1039
1041	1040
1041	1041
1041	1042
1041	1043
1041	1044
1041	1046
1041	1047
1041	1048
1041	1049
1041	1050
1041	1051
1041	1052
1041	1053
1041	1055
1041	1056
1041	1057
1041	1058
1041	1059
1041	1060
1041	1061
1041	1062
1041	1064
1041	1065
1041	1066
1041	1067
1041	1068
1041	1069
1041	1070
1041	1071
1041	1073
1041	1074
1041	1075
1041	1076
1041	1077
1041	1078
1041	1079
1041	1080
1041	1082
1041	1083
1041	1084
1041	1085
1041	1086
1041	1190
\.


--
-- Data for Name: dish; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status, created_on, reference) FROM stdin;
1009	\N	tagTest	\N	\N	\N	\N	\N
1010	\N	tagTest	\N	\N	\N	\N	\N
1011	\N	tagTest	\N	\N	\N	\N	\N
1012	\N	tagTest	\N	\N	\N	\N	\N
1013	\N	tagTest	\N	\N	\N	\N	\N
1014	\N	tagTest	\N	\N	\N	\N	\N
1015	\N	tagTest	\N	\N	\N	\N	\N
1016	\N	tagTest	\N	\N	\N	\N	\N
1017	\N	tagTest	\N	\N	\N	\N	\N
1018	\N	tagTest	\N	\N	\N	\N	\N
1019	\N	tagTest	\N	\N	\N	\N	\N
1020	\N	tagTest	\N	\N	\N	\N	\N
1	\N	Israeli CousIIcous	20	\N	105	2021-04-11 09:45:31.48136+00	\N
2	\N	Breakfast Casserole	20	\N	105	2021-04-11 09:44:31.48136+00	\N
3	\N	dijon-tarragon cream chicken	20	\N	105	2021-04-11 09:43:31.48136+00	\N
4	\N	Thai Chicken with Basil Stir-Fry	20	\N	\N	2021-04-11 09:42:31.48136+00	\N
5	\N	red beans and rice	20	\N	\N	2021-04-11 09:41:31.48136+00	\N
6	\N	chicken estragon	20	\N	\N	2021-04-11 09:40:31.48136+00	\N
7	\N	quick chicken curry	20	\N	\N	2021-04-11 09:39:31.48136+00	\N
8	\N	spicy pumpkin chili	20	\N	\N	2021-04-11 09:38:31.48136+00	\N
9	\N	ginger beef	20	\N	\N	2021-04-11 09:37:31.48136+00	\N
10	\N	four vegetable soup	20	\N	\N	2021-04-11 09:36:31.48136+00	\N
11	\N	seared pork chops with mushroom gravy	20	\N	\N	2021-04-11 09:35:31.48136+00	\N
12	\N	greek island chicken	20	\N	\N	2021-04-11 09:34:31.48136+00	\N
14	\N	Pecan-Crusted Pork with Pumpkin Butter	20	\N	\N	2021-04-11 09:32:31.48136+00	\N
15	\N	Chef John's Salmon Cakes	20	\N	\N	2021-04-11 09:31:31.48136+00	\N
17	\N	pasta with butternut squash, spinach and prosciutto	20	\N	\N	2021-04-11 09:29:31.48136+00	\N
18	\N	cod with leeks, tomatoes, and olives	20	\N	\N	2021-04-11 09:28:31.48136+00	\N
19	\N	lamb and eggplant pasta with goat cheese	20	\N	\N	2021-04-11 09:27:31.48136+00	\N
20	\N	skillet lemon chicken with rice and peas	20	\N	\N	2021-04-11 09:26:31.48136+00	\N
21	\N	steak with blue cheese butter and sour cream potatoes	20	\N	\N	2021-04-11 09:25:31.48136+00	\N
22	\N	pork chops with roasted red pepper cream	20	\N	\N	2021-04-11 09:24:31.48136+00	\N
23	\N	cod with tarragon and potatoes	20	\N	\N	2021-04-11 09:23:31.48136+00	\N
47	\N	Chicken Cassolet	20	\N	\N	2021-04-11 08:59:31.48136+00	\N
48	\N	quick pasta primavera	20	\N	\N	2021-04-11 08:58:31.48136+00	\N
49	\N	moroccan chicken soup	20	\N	\N	2021-04-11 08:57:31.48136+00	\N
50	\N	farfalla with sun-dried tomatoes, arugula and goat cheese	20	\N	\N	2021-04-11 08:56:31.48136+00	\N
51	\N	pan-seared chicken with mushrooms and boursin	20	\N	\N	2021-04-11 08:55:31.48136+00	\N
52	\N	crispy dijon chicken breasts	20	\N	\N	2021-04-11 08:54:31.48136+00	\N
53	\N	pasta with lemony chicken and asparagus	20	\N	\N	2021-04-11 08:53:31.48136+00	\N
54	\N	Broccoli Beef	20	\N	\N	2021-04-11 08:52:31.48136+00	\N
55	\N	Chef John's Beef Goulash	20	\N	\N	2021-04-11 08:51:31.48136+00	\N
56	\N	Hoppin' John	20	\N	\N	2021-04-11 08:50:31.48136+00	\N
57	\N	snickerdoodles	20	\N	\N	2021-04-11 08:49:31.48136+00	\N
58	\N	hot spiced cider	20	\N	\N	2021-04-11 08:48:31.48136+00	\N
59	\N	buckeyes	20	\N	\N	2021-04-11 08:47:31.48136+00	\N
60	\N	Salmon with Asparagus and Chive Butter Sauce	20	\N	\N	2021-04-11 08:46:31.48136+00	\N
61	\N	Slow Cooker Beef Stew	20	\N	\N	2021-04-11 08:45:31.48136+00	\N
62	\N	Fish Tacos	20	\N	\N	2021-04-11 08:44:31.48136+00	\N
63	\N	Tuna and Pasta Salad	20	\N	\N	2021-04-11 08:43:31.48136+00	\N
64	\N	Chef John's Chicken Kiev	20	\N	\N	2021-04-11 08:42:31.48136+00	\N
99	\N	Swedish Beef Crockpot	20	\N	\N	2021-04-11 08:07:31.48136+00	\N
106	\N	Quiche Lorraine	20	\N	\N	2021-04-11 08:00:31.48136+00	\N
13	\N	green chili	20	2018-01-12 22:00:00+00	\N	2021-04-11 09:33:31.48136+00	\N
16	\N	Cheeseburger Macaroni	20	2018-02-03 06:22:03.125+00	\N	2021-04-11 09:30:31.48136+00	\N
24	\N	ble	20	2018-01-27 22:00:00+00	\N	2021-04-11 09:22:31.48136+00	\N
25	\N	Ham and Potato Soup	20	2018-02-24 11:33:40.506+00	\N	2021-04-11 09:21:31.48136+00	\N
26	\N	slow cooker black bean soup	20	2018-02-07 22:00:00+00	\N	2021-04-11 09:20:31.48136+00	\N
27	\N	tuna casserole I	20	2017-12-08 22:00:00+00	\N	2021-04-11 09:19:31.48136+00	\N
28	\N	Porcupine Meatballs	20	2018-01-12 22:00:00+00	\N	2021-04-11 09:18:31.48136+00	\N
29	\N	Broiled Salmon with Potato Crust	20	2018-01-05 22:00:00+00	\N	2021-04-11 09:17:31.48136+00	\N
30	\N	chocolate crinkles	20	2017-12-09 22:00:00+00	\N	2021-04-11 09:16:31.48136+00	\N
31	\N	garbanzo bean salad	20	2017-11-17 22:00:00+00	\N	2021-04-11 09:15:31.48136+00	\N
32	\N	christmas bread	20	2017-12-22 22:00:00+00	\N	2021-04-11 09:14:31.48136+00	\N
33	\N	homemade eggnog	20	2017-12-09 22:00:00+00	\N	2021-04-11 09:13:31.48136+00	\N
34	\N	ginger candied carrots	20	2017-12-22 22:00:00+00	\N	2021-04-11 09:12:31.48136+00	\N
35	\N	pecan pie cookies	20	2017-12-09 22:00:00+00	\N	2021-04-11 09:11:31.48136+00	\N
36	\N	Frozen Dinner	20	2018-02-24 11:33:40.506+00	\N	2021-04-11 09:10:31.48136+00	\N
37	\N	lentils and spicy sausage	20	2018-01-27 22:00:00+00	\N	2021-04-11 09:09:31.48136+00	\N
38	\N	tuna caper spaghetti	20	2017-12-08 22:00:00+00	\N	2021-04-11 09:08:31.48136+00	\N
39	\N	carbonara	20	2018-02-02 22:00:00+00	\N	2021-04-11 09:07:31.48136+00	\N
40	\N	Split Pea and Ham Soup	20	2017-12-01 22:00:00+00	\N	2021-04-11 09:06:31.48136+00	\N
41	\N	pizzadillas	20	2018-01-12 22:00:00+00	\N	2021-04-11 09:05:31.48136+00	\N
42	\N	Lentil and Bulgur Pilaf	20	2018-01-12 22:00:00+00	\N	2021-04-11 09:04:31.48136+00	\N
43	\N	arrugula gnocchi	20	2018-02-07 22:00:00+00	\N	2021-04-11 09:03:31.48136+00	\N
44	\N	Red Chili	20	2018-02-10 16:56:28.335+00	\N	2021-04-11 09:02:31.48136+00	\N
45	\N	broccoli and carrots	20	2018-02-10 16:56:28.335+00	\N	2021-04-11 09:01:31.48136+00	\N
46	\N	curry lentils	20	2018-02-10 16:56:28.335+00	\N	2021-04-11 09:00:31.48136+00	\N
65	\N	Slow Cooker Chicken Chili	20	2018-02-02 22:00:00+00	\N	2021-04-11 08:41:31.48136+00	\N
66	\N	Vegetarian Korma	20	2018-02-03 06:22:03.125+00	\N	2021-04-11 08:40:31.48136+00	\N
67	\N	Pie Crust	20	2017-11-20 22:00:00+00	\N	2021-04-11 08:39:31.48136+00	\N
68	\N	Sweet Potato Casserole	20	2017-11-20 22:00:00+00	\N	2021-04-11 08:38:31.48136+00	\N
69	\N	Zucchini Soup	20	2018-01-19 22:00:00+00	\N	2021-04-11 08:37:31.48136+00	\N
70	\N	pan-seared cod with herb butter sauce	20	2018-01-27 22:00:00+00	\N	2021-04-11 08:36:31.48136+00	\N
71	\N	pecan sandies	20	2017-12-09 22:00:00+00	\N	2021-04-11 08:35:31.48136+00	\N
72	\N	Pumpkin Pie filling	20	2017-11-20 22:00:00+00	\N	2021-04-11 08:34:31.48136+00	\N
73	\N	fudge	20	2017-12-09 22:00:00+00	\N	2021-04-11 08:33:31.48136+00	\N
74	\N	couscous	20	2017-12-08 22:00:00+00	\N	2021-04-11 08:32:31.48136+00	\N
75	\N	cranberry sauce	20	2017-11-20 22:00:00+00	\N	2021-04-11 08:31:31.48136+00	\N
76	\N	beef pumpkin stew	20	2018-01-27 22:00:00+00	\N	2021-04-11 08:30:31.48136+00	\N
77	\N	Beef and Guiness Stew	20	2017-11-10 22:00:00+00	\N	2021-04-11 08:29:31.48136+00	\N
78	\N	Tomato Bacon Pasta	20	2018-01-19 22:00:00+00	\N	2021-04-11 08:28:31.48136+00	\N
79	\N	gingerbread	20	2017-12-09 22:00:00+00	\N	2021-04-11 08:27:31.48136+00	\N
80	\N	moroccan stew	20	2017-12-15 22:00:00+00	\N	2021-04-11 08:26:31.48136+00	\N
81	\N	Crock Pot Chicken Jambalaya	20	2018-01-19 22:00:00+00	\N	2021-04-11 08:25:31.48136+00	\N
82	\N	Crock Pot Olive Garden Pasta	20	2017-11-17 22:00:00+00	\N	2021-04-11 08:24:31.48136+00	\N
83	\N	Kate Salad	20	2018-01-05 22:00:00+00	\N	2021-04-11 08:23:31.48136+00	\N
84	\N	Prosciutto-Wrapped Cod	20	2017-11-17 22:00:00+00	\N	2021-04-11 08:22:31.48136+00	\N
85	\N	cod with mediterranean salsa	20	2017-11-10 22:00:00+00	\N	2021-04-11 08:21:31.48136+00	\N
86	\N	pea and ham pasta	20	2018-02-24 11:33:40.506+00	\N	2021-04-11 08:20:31.48136+00	\N
87	\N	mixed veggies	20	2017-11-17 22:00:00+00	\N	2021-04-11 08:19:31.48136+00	\N
88	\N	golden chicken rice	20	2018-01-05 22:00:00+00	\N	2021-04-11 08:18:31.48136+00	\N
89	\N	Gulliver's Creamed Corn	20	2017-11-20 22:00:00+00	\N	2021-04-11 08:17:31.48136+00	\N
90	\N	scoozi	20	2017-12-01 22:00:00+00	\N	2021-04-11 08:16:31.48136+00	\N
91	\N	red pepper risotto	20	2018-02-24 11:33:40.506+00	\N	2021-04-11 08:15:31.48136+00	\N
92	\N	Mom's Stuffing	20	2017-11-20 22:00:00+00	\N	2021-04-11 08:14:31.48136+00	\N
93	\N	Turkey Gravy	20	2017-11-20 22:00:00+00	\N	2021-04-11 08:13:31.48136+00	\N
94	\N	madame farfalla	20	2017-11-10 22:00:00+00	\N	2021-04-11 08:12:31.48136+00	\N
95	\N	French Onion Soup	20	2017-12-22 22:00:00+00	\N	2021-04-11 08:11:31.48136+00	\N
96	\N	heuvos rancheros	20	2018-01-27 22:00:00+00	\N	2021-04-11 08:10:31.48136+00	\N
97	\N	mashed potatoes	20	2018-02-24 11:33:40.506+00	\N	2021-04-11 08:09:31.48136+00	\N
98	\N	quick burgers and pasta	20	2018-02-10 16:56:28.335+00	\N	2021-04-11 08:08:31.48136+00	\N
100	\N	crispy cucumber salad	20	2018-01-27 22:00:00+00	\N	2021-04-11 08:06:31.48136+00	\N
101	\N	crockpot corn chowder	20	2018-02-10 16:56:28.335+00	\N	2021-04-11 08:05:31.48136+00	\N
102	\N	quiche lorraine	20	2018-02-17 09:22:14.13+00	\N	2021-04-11 08:04:31.48136+00	\N
103	\N	Cream of Roasted Carrot Soup	20	2018-02-17 09:22:14.13+00	\N	2021-04-11 08:03:31.48136+00	\N
104	\N	schnitzel with sauce	20	2018-02-24 11:33:40.506+00	\N	2021-04-11 08:02:31.48136+00	\N
105	\N	Chicken Flautas	20	2018-02-24 11:33:40.506+00	\N	2021-04-11 08:01:31.48136+00	\N
107	\N	tuna casserole II	20	2018-02-17 09:22:14.13+00	\N	2021-04-11 07:59:31.48136+00	\N
108	\N	chocolate chip cookies	20	2018-02-17 09:22:14.13+00	\N	2021-04-11 07:58:31.48136+00	\N
109	\N	Stir Fry - Mostly Green with Orange	20	2018-02-17 09:22:14.13+00	\N	2021-04-11 07:57:31.48136+00	\N
110	\N	Jack Salad	20	2018-02-17 09:22:14.13+00	\N	2021-04-11 07:56:31.48136+00	\N
111	\N	to delete	20	2018-02-24 11:21:01.186+00	\N	2021-04-11 07:55:31.48136+00	\N
112	\N	side salad	20	2018-02-24 11:33:40.506+00	\N	2021-04-11 07:54:31.48136+00	\N
113	\N	Side of Rice	20	2018-02-24 11:33:40.506+00	\N	2021-04-11 07:53:31.48136+00	\N
114	\N	peas, carrots and corn	20	2018-02-24 11:33:40.506+00	\N	2021-04-11 07:52:31.48136+00	\N
115	\N	Boo-yah	20	2018-02-24 11:33:40.506+00	\N	2021-04-11 07:51:31.48136+00	\N
500	\N	dish1	500	\N	\N	\N	\N
501	\N	dish2	500	\N	\N	\N	\N
502	\N	dish3	500	\N	\N	\N	\N
503	\N	dish4	500	\N	\N	\N	\N
603	\N	dish4	500	\N	\N	\N	\N
504	\N	dish4	500	\N	\N	\N	\N
1000	\N	tagTest	\N	\N	\N	\N	\N
1001	\N	tagTest	\N	\N	\N	\N	\N
1002	\N	tagTest	\N	\N	\N	\N	\N
1003	\N	tagTest	\N	\N	\N	\N	\N
1004	\N	tagTest	\N	\N	\N	\N	\N
1005	\N	tagTest	\N	\N	\N	\N	\N
1006	\N	tagTest	\N	\N	\N	\N	\N
1007	\N	tagTest	\N	\N	\N	\N	\N
1008	\N	tagTest	\N	\N	\N	\N	\N
\.


--
-- Data for Name: dish_items; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.dish_items (dish_item_id, dish_id, tag_id, whole_quantity, fractional_quantity, quantity, unit_id, marker, unit_size, raw_modifiers, raw_entry, modifiers_processed, user_size) FROM stdin;
1000	109	13	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1001	109	15	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1002	115	15	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1003	109	16	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1004	115	16	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1005	109	19	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1006	109	21	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1007	114	25	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1008	115	25	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1009	112	32	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1010	110	33	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1011	112	33	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1012	110	34	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1013	112	34	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1014	110	36	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1015	109	41	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1016	115	59	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1017	109	81	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1018	112	81	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1019	114	81	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1020	115	81	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1021	115	101	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1022	115	110	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1023	113	123	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1024	114	155	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1025	115	165	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1026	115	185	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1027	115	187	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1028	111	199	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1029	112	199	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1030	113	199	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1031	114	199	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1032	109	212	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1033	109	217	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1034	114	245	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1035	115	251	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1036	109	256	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1037	115	301	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1038	115	307	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1039	115	315	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1040	109	318	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1041	109	320	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1042	111	320	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1043	115	320	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1044	115	321	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1045	115	328	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1046	110	336	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1047	108	337	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1048	108	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1049	109	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1050	110	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1051	111	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1052	112	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1053	113	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1054	114	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1055	115	346	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1056	115	346	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1057	108	348	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1058	108	350	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1059	108	360	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1060	115	360	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1061	108	361	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1062	115	397	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1063	115	400	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1064	115	419	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1065	115	426	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1066	110	432	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1067	112	432	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1068	113	432	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1069	114	432	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1070	108	433	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1071	96	1	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1072	74	2	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1073	101	2	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1074	101	2	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1075	103	2	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1076	103	2	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1077	106	4	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1078	105	11	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1079	28	12	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1080	5	13	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1081	27	13	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1082	28	13	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1083	56	13	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1084	88	13	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1085	5	15	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1086	10	15	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1087	26	15	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1088	28	15	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1089	40	15	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1090	77	15	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1091	81	15	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1092	82	15	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1093	92	15	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1094	101	15	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1095	101	15	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1096	107	15	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1097	4	16	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1098	5	16	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1099	6	16	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1100	8	16	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1101	10	16	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1102	11	16	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1103	12	16	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1104	13	16	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1105	16	16	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1106	19	16	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1107	20	16	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1108	22	16	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1109	24	16	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1110	26	16	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1111	28	16	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1112	37	16	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1113	38	16	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1114	40	16	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1115	42	16	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1116	44	16	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1117	46	16	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1118	47	16	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1119	49	16	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1120	55	16	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1121	56	16	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1122	61	16	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1123	63	16	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1124	65	16	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1125	66	16	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1126	69	16	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1127	77	16	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1128	78	16	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1129	80	16	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1130	81	16	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1131	82	16	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1132	86	16	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1133	88	16	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1134	91	16	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1135	92	16	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1136	93	16	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1137	95	16	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1138	99	16	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1139	103	16	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1140	105	16	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1141	107	16	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1142	28	17	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1143	2	18	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1144	13	18	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1145	16	18	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1146	44	18	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1147	56	18	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1148	95	18	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1149	96	18	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1150	107	18	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1151	4	19	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1152	6	19	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1153	8	19	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1154	10	19	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1155	12	19	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1156	13	19	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1157	16	19	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1158	18	19	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1159	19	19	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1160	20	19	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1161	22	19	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1162	23	19	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1163	26	19	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1164	37	19	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1165	39	19	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1166	40	19	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1167	42	19	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1168	44	19	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1169	47	19	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1170	48	19	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1171	50	19	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1172	53	19	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1173	1009	1088	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1174	55	19	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1175	61	19	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1176	64	19	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1177	65	19	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1178	66	19	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1179	77	19	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1180	78	19	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1181	80	19	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1182	84	19	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1183	86	19	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1184	91	19	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1185	103	19	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1186	105	19	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1187	107	19	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1188	16	20	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1189	45	21	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1190	1009	1089	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1191	90	21	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1192	90	22	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1193	6	23	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1194	6	23	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1195	12	23	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1196	17	23	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1197	18	23	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1198	51	23	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1199	60	23	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1200	70	23	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1201	86	23	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1202	90	23	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1203	91	23	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1204	93	23	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1205	103	23	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1206	9	24	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1207	17	24	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1208	38	24	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1209	39	24	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1210	43	24	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1211	48	24	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1212	53	24	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1213	78	24	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1214	82	24	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1215	86	24	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1216	89	25	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1217	101	25	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1218	101	25	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1219	5	26	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1220	17	27	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1221	84	27	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1222	56	28	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1223	5	28	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1224	65	29	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1225	82	29	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1226	5	29	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1227	19	30	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1228	94	31	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1229	94	32	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1230	94	34	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1231	105	36	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1232	1010	1097	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1233	29	38	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1234	60	38	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1235	18	46	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1236	23	46	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1237	70	46	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1238	84	46	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1239	85	46	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1240	15	47	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1241	55	47	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1242	64	47	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1243	79	47	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1244	81	47	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1245	89	47	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1246	21	48	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1247	21	48	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1248	91	49	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1249	6	49	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1250	6	49	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1251	80	50	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1252	91	50	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1253	6	50	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1254	6	50	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1255	6	51	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1256	6	51	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1257	23	52	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1258	6	52	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1259	6	52	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1260	10	52	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1261	6	53	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1262	6	53	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1263	34	54	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1264	55	54	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1265	10	55	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1266	51	55	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1267	70	55	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1268	85	55	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1269	1010	1098	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1270	10	56	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1271	10	57	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1272	18	57	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1273	40	57	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1274	42	57	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1275	55	57	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1276	103	57	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1277	10	58	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1278	17	58	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1279	40	58	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1280	70	58	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1281	77	58	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1282	93	58	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1283	10	59	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1284	21	59	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1285	21	59	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1286	23	59	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1287	10	60	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1288	55	61	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1289	76	61	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1290	107	61	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1291	9	61	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1292	14	62	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1293	15	62	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1294	23	62	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1295	27	62	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1296	52	62	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1297	64	62	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1298	104	62	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1299	107	62	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1300	31	64	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1301	42	64	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1302	66	64	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1303	105	64	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1304	106	64	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1305	5	64	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1306	27	67	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1307	32	67	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1308	33	67	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1309	57	67	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1310	72	67	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1311	73	67	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1312	89	67	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1313	101	67	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1314	101	67	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1315	107	67	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1316	2	69	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1317	15	69	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1318	30	69	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1319	32	69	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1320	33	69	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1321	57	69	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1322	64	69	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1323	68	69	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1324	72	69	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1325	79	69	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1326	92	69	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1327	96	69	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1328	102	69	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1329	104	69	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1330	106	69	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1331	27	71	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1332	102	71	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1333	106	71	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1334	102	72	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1335	106	73	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1336	40	74	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1337	77	74	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1338	78	74	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1339	102	74	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1340	100	75	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1341	94	76	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1342	100	76	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1343	100	77	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1344	37	78	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1345	57	79	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1346	79	79	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1347	108	79	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1348	108	80	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1349	5	81	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1350	10	81	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1351	26	81	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1352	34	81	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1353	40	81	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1354	45	81	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1355	61	81	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1356	63	81	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1357	66	81	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1358	76	81	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1359	77	81	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1360	82	81	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1361	87	81	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1362	88	81	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1363	103	81	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1364	26	82	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1365	32	84	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1366	105	85	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1367	106	85	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1368	37	89	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1369	96	89	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1370	98	90	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1371	98	91	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1372	98	92	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1373	15	95	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1374	75	97	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1375	18	99	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1376	85	99	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1377	85	100	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1378	4	101	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1379	19	101	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1380	22	101	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1381	41	101	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1382	85	101	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1383	86	101	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1384	15	102	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1385	38	102	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1386	84	102	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1387	85	102	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1388	85	103	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1389	1011	1106	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1390	20	104	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1391	23	104	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1392	27	104	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1393	42	104	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1394	48	104	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1395	53	104	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1396	70	104	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1397	85	104	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1398	82	105	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1399	94	105	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1400	94	106	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1401	94	107	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1402	48	108	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1403	48	109	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1404	53	109	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1405	60	109	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1406	48	110	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1407	78	110	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1408	17	111	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1409	48	111	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1410	49	112	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1411	49	113	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1412	80	113	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1413	83	113	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1414	1011	1107	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1415	49	114	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1416	74	114	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1417	49	115	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1418	17	116	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1419	17	117	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1420	53	117	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1421	18	118	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1422	18	119	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1423	19	120	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1424	19	121	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1425	19	122	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1426	50	122	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1427	20	123	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1428	62	47	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1429	21	124	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1430	21	124	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1431	21	125	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1432	21	125	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1433	3	126	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1434	22	126	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1435	66	126	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1436	72	126	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1437	22	127	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1438	22	128	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1439	23	129	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1440	52	129	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1441	50	130	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1442	50	131	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1443	53	131	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1444	86	131	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1445	91	131	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1446	50	132	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1447	50	133	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1448	55	133	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1449	50	134	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1450	78	134	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1451	62	69	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1452	50	135	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1453	67	142	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1454	68	142	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1455	72	142	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1456	75	142	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1457	89	142	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1458	92	142	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1459	93	142	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1460	24	155	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1461	31	155	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1462	37	155	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1463	39	155	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1464	39	155	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1465	39	155	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1466	39	155	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1467	39	155	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1468	69	155	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1469	107	155	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1470	93	159	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1471	86	161	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1472	86	162	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1473	86	163	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1474	82	164	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1475	62	102	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1476	61	165	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1477	82	165	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1478	95	165	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1479	38	166	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1480	41	166	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1481	82	166	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1482	61	167	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1483	68	167	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1484	80	167	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1485	27	168	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1486	44	168	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1487	55	168	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1488	65	168	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1489	65	169	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1490	89	170	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1491	89	171	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1492	92	172	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1493	92	173	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1494	79	174	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1495	73	175	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1496	73	176	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1497	73	177	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1498	35	178	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1499	35	179	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1500	62	129	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1501	55	181	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1502	57	181	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1503	67	181	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1504	68	181	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1505	71	181	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1506	73	181	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1507	75	181	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1508	77	181	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1509	79	181	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1510	89	181	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1511	95	181	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1512	100	181	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1513	108	181	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1514	62	185	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1515	14	181	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1516	30	181	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1517	32	181	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1518	33	181	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1519	35	181	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1520	57	182	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1521	68	182	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1522	71	182	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1523	73	182	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1524	108	182	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1525	30	182	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1526	33	182	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1527	35	182	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1528	26	183	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1529	65	184	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1530	8	184	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1531	13	184	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1532	26	184	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1533	44	184	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1534	81	185	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1535	82	185	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1536	26	185	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1537	80	186	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1538	103	186	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1539	26	186	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1540	43	186	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1541	66	187	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1542	81	187	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1543	87	187	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1544	8	187	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1545	44	187	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1546	82	188	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1547	8	188	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1548	44	188	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1549	65	189	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1550	8	189	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1551	66	190	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1552	76	190	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1553	8	190	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1554	72	191	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1555	8	191	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1556	61	192	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1557	68	192	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1558	72	192	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1559	102	192	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1560	106	192	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1561	8	192	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1562	33	192	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1563	43	192	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1564	53	193	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1565	72	193	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1566	8	193	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1567	14	193	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1568	72	194	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1569	79	194	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1570	8	194	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1571	14	194	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1572	34	194	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1573	105	198	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1574	62	199	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1575	57	199	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1576	58	199	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1577	60	199	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1578	63	199	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1579	66	199	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1580	74	199	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1581	80	199	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1582	83	199	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1583	95	199	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1584	99	199	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1585	62	204	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1586	9	199	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1587	14	199	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1588	15	199	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1589	27	199	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1590	31	199	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1591	33	199	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1592	38	199	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1593	41	199	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1594	43	199	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1595	45	199	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1596	43	200	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1597	43	201	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1598	43	202	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1599	52	203	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1600	65	203	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1601	99	203	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1602	100	203	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1603	21	203	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1604	21	203	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1605	43	203	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1606	60	204	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1607	70	204	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1608	84	204	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1609	85	204	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1610	18	204	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1611	23	204	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1612	29	204	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1613	29	205	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1614	29	206	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1615	92	206	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1616	2	206	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1617	29	207	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1618	20	208	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1619	29	208	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1620	70	208	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1621	31	209	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1622	27	210	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1623	31	210	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1624	38	210	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1625	83	210	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1626	107	210	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1627	31	211	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1628	83	211	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1629	100	211	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1630	4	212	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1631	9	212	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1632	62	238	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1633	66	212	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1634	75	212	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1635	76	212	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1636	80	212	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1637	75	213	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1638	16	218	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1639	66	219	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1640	104	219	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1641	66	227	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1642	97	227	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1643	101	227	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1644	101	227	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1645	14	229	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1646	35	229	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1647	68	229	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1648	71	229	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1649	81	230	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1650	81	231	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1651	81	232	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1652	40	233	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1653	40	234	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1654	9	237	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1655	59	237	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1656	13	238	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1657	41	238	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1658	96	238	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1659	79	242	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1660	80	242	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1661	88	242	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1662	14	243	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1663	57	243	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1664	80	243	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1665	24	244	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1666	20	245	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1667	48	245	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1668	66	245	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1669	86	245	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1670	107	245	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1671	62	255	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1672	10	245	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1673	42	247	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1674	51	247	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1675	64	247	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1676	70	247	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1677	76	247	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1678	82	247	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1679	91	247	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1680	92	247	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1681	93	247	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1682	51	248	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1683	52	249	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1684	60	249	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1685	52	250	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1686	9	251	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1687	61	251	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1688	76	251	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1689	77	251	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1690	99	251	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1691	77	252	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1692	13	255	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1693	80	255	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1694	4	256	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1695	9	256	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1696	62	284	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1697	62	293	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1698	55	258	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1699	55	259	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1700	56	260	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1701	62	320	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1702	62	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1703	62	347	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1704	88	263	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1705	104	264	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1706	2	267	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1707	30	267	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1708	32	267	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1709	33	267	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1710	59	267	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1711	71	267	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1712	68	268	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1713	75	268	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1714	89	268	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1715	92	268	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1716	42	271	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1717	58	271	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1718	58	272	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1719	79	272	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1720	58	273	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1721	30	274	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1722	95	278	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1723	95	279	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1724	95	282	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1725	13	284	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1726	66	284	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1727	66	285	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1728	46	286	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1729	66	286	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1730	88	286	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1731	42	287	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1732	66	287	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1733	3	288	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1734	76	289	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1735	30	293	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1736	30	294	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1737	59	294	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1738	71	294	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1739	59	295	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1740	79	296	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1741	79	297	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1742	10	301	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1743	61	301	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1744	66	301	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1745	69	301	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1746	76	301	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1747	80	301	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1748	82	301	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1749	95	301	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1750	95	301	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1751	17	303	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1752	43	303	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1753	50	303	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1754	53	303	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1755	86	303	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1756	76	308	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1757	76	309	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1758	14	310	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1759	42	310	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1760	76	310	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1761	13	311	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1762	13	312	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1763	15	315	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1764	36	315	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1765	77	315	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1766	85	315	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1767	39	316	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1768	44	317	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1769	101	317	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1770	101	317	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1771	2	320	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1772	3	320	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1773	4	320	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1774	5	320	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1775	6	320	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1776	7	320	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1777	8	320	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1778	9	320	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1779	10	320	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1780	11	320	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1781	12	320	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1782	13	320	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1783	14	320	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1784	15	320	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1785	16	320	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1786	17	320	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1787	18	320	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1788	19	320	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1789	20	320	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1790	21	320	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1791	21	320	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1792	22	320	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1793	23	320	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1794	26	320	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1795	27	320	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1796	28	320	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1797	29	320	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1798	36	320	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1799	37	320	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1800	38	320	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1801	39	320	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1802	40	320	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1803	41	320	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1804	42	320	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1805	43	320	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1806	44	320	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1807	46	320	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1808	47	320	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1809	48	320	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1810	49	320	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1811	50	320	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1812	50	320	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1813	51	320	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1814	52	320	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1815	53	320	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1816	62	350	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1817	55	320	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1818	56	320	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1819	60	320	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1820	61	320	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1821	63	320	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1822	64	320	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1823	65	320	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1824	66	320	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1825	69	320	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1826	70	320	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1827	76	320	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1828	77	320	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1829	78	320	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1830	80	320	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1831	81	320	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1832	82	320	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1833	84	320	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1834	85	320	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1835	86	320	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1836	88	320	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1837	90	320	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1838	91	320	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1839	94	320	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1840	95	320	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1841	96	320	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1842	98	320	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1843	99	320	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1844	101	320	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1845	101	320	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1846	103	320	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1847	104	320	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1848	105	320	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1849	106	320	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1850	107	320	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1851	39	321	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1852	66	321	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1853	103	321	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1854	106	321	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1855	65	322	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1856	105	322	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1857	5	323	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1858	26	323	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1859	47	323	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1860	61	323	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1861	65	323	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1862	76	323	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1863	77	323	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1864	80	323	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1865	81	323	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1866	82	323	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1867	99	323	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1868	101	323	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1869	101	323	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1870	16	324	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1871	76	324	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1872	104	324	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1873	77	325	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1874	16	327	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1875	39	327	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1876	104	327	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1877	103	328	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1878	106	328	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1879	39	330	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1880	62	360	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1881	12	334	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1882	15	334	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1883	28	334	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1884	42	334	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1885	43	334	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1886	51	334	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1887	52	334	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1888	53	334	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1889	55	334	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1890	56	334	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1891	60	334	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1892	61	334	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1893	63	334	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1894	64	334	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1895	65	334	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1896	68	334	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1897	70	334	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1898	76	334	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1899	77	334	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1900	80	334	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1901	82	334	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1902	86	334	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1903	91	334	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1904	92	334	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1905	97	334	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1906	99	334	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1907	100	334	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1908	102	334	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1909	103	334	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1910	62	460	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1911	4	335	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1912	8	335	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1913	9	335	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1914	38	335	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1915	39	335	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1916	56	335	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1917	62	461	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1918	3	336	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1919	4	336	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1920	9	336	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1921	12	336	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1922	15	336	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1923	18	336	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1924	19	336	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1925	23	336	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1926	29	336	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1927	31	336	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1928	38	336	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1929	39	336	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1930	42	336	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1931	47	336	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1932	48	336	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1933	52	336	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1934	55	336	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1935	61	336	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1936	63	336	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1937	74	336	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1938	83	336	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1939	85	336	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1940	91	336	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1941	28	337	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1942	39	337	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1943	8	338	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1944	47	338	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1945	65	338	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1946	2	340	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1947	5	340	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1948	46	340	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1949	47	340	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1950	47	341	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1951	55	341	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1952	77	341	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1953	86	341	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1954	42	342	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1955	49	342	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1956	69	342	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1957	87	342	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1958	8	343	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1959	26	343	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1960	65	343	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1961	69	343	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1962	62	462	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1963	2	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1964	3	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1965	4	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1966	6	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1967	7	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1968	8	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1969	9	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1970	10	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1971	11	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1972	12	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1973	13	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1974	14	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1975	16	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1976	17	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1977	18	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1978	19	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1979	20	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1980	21	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1981	22	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1982	23	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1983	24	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1984	26	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1985	27	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1986	28	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1987	30	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1988	32	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1989	33	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1990	34	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1991	35	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1992	37	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1993	38	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1994	39	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1995	40	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1996	41	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1997	43	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1998	44	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
1999	45	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2000	47	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2001	48	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2002	49	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2003	50	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2004	51	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2005	52	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2006	53	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2007	62	463	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2008	55	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2009	56	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2010	57	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2011	58	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2012	61	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2013	63	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2014	64	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2015	65	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2016	67	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2017	68	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2018	69	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2019	70	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2020	71	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2021	72	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2022	73	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2023	75	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2024	76	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2025	78	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2026	79	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2027	80	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2028	81	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2029	82	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2030	83	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2031	86	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2032	87	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2033	88	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2034	89	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2035	90	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2036	91	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2037	92	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2038	93	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2039	94	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2040	95	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2041	96	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2042	97	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2043	98	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2044	99	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2045	100	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2046	102	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2047	104	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2048	107	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2049	69	345	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2050	2	346	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2051	3	346	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2052	4	346	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2053	8	346	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2054	9	346	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2055	11	346	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2056	13	346	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2057	42	346	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2058	44	346	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2059	55	346	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2060	61	346	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2061	64	346	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2062	76	346	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2063	78	346	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2064	88	346	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2065	91	346	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2066	96	346	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2067	99	346	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2068	104	346	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2069	105	346	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2070	11	347	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2071	13	347	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2072	14	347	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2073	20	347	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2074	21	347	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2075	21	347	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2076	22	347	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2077	30	347	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2078	49	347	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2079	55	347	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2080	64	347	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2081	65	347	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2082	66	347	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2083	70	347	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2084	76	347	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2085	84	347	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2086	88	347	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2087	93	347	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2088	103	347	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2089	105	347	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2090	3	348	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2091	6	348	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2092	6	348	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2093	11	348	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2094	15	348	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2095	17	348	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2096	20	348	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2097	21	348	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2098	21	348	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2099	27	348	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2100	32	348	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2101	34	348	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2102	35	348	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2103	40	348	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2104	40	348	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2105	51	348	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2106	53	348	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2107	57	348	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2108	59	348	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2109	60	348	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2110	61	348	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2111	64	348	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2112	67	348	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2113	68	348	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2114	70	348	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2115	71	348	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2116	73	348	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2117	76	348	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2118	79	348	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2119	84	348	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2120	89	348	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2121	92	348	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2122	93	348	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2123	95	348	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2124	97	348	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2125	101	348	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2126	101	348	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2127	104	348	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2128	107	348	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2129	11	349	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2130	51	349	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2131	107	349	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2132	11	350	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2133	13	350	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2134	27	350	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2135	30	350	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2136	32	350	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2137	35	350	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2138	62	464	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2139	57	350	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2140	61	350	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2141	64	350	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2142	67	350	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2143	68	350	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2144	70	350	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2145	71	350	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2146	76	350	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2147	79	350	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2148	89	350	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2149	93	350	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2150	99	350	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2151	104	350	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2152	107	350	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2153	11	351	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2154	47	351	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2155	55	351	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2156	62	465	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2157	3	352	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2158	11	352	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2159	23	352	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2160	29	352	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2161	52	352	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2162	8	353	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2163	13	353	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2164	18	353	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2165	19	353	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2166	44	353	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2167	47	353	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2168	49	353	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2169	65	353	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2170	80	353	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2171	82	353	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2172	86	353	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2173	6	354	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2174	6	354	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2175	10	354	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2176	12	354	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2177	47	354	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2178	53	354	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2179	63	354	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2180	92	354	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2181	8	355	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2182	12	355	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2183	66	355	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2184	80	355	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2185	91	355	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2186	12	356	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2187	38	356	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2188	94	356	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2189	12	357	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2190	15	357	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2191	68	357	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2192	84	357	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2193	8	358	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2194	12	358	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2195	72	358	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2196	75	358	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2197	79	358	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2198	12	359	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2199	79	359	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2200	62	353	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2201	8	360	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2202	12	360	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2203	15	360	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2204	30	360	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2205	32	360	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2206	42	360	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2207	51	360	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2208	52	360	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2209	53	360	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2210	55	360	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2211	56	360	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2212	57	360	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2213	60	360	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2214	61	360	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2215	63	360	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2216	64	360	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2217	66	360	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2218	67	360	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2219	68	360	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2220	70	360	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2221	71	360	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2222	72	360	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2223	75	360	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2224	76	360	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2225	77	360	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2226	80	360	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2227	88	360	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2228	89	360	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2229	91	360	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2230	92	360	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2231	97	360	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2232	99	360	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2233	100	360	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2234	102	360	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2235	103	360	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2236	58	361	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2237	68	361	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2238	72	361	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2239	76	361	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2240	80	361	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2241	44	363	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2242	65	363	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2243	105	363	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2244	66	364	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2245	42	368	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2246	64	368	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2247	99	368	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2248	104	395	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2249	39	396	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2250	66	396	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2251	103	396	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2252	105	396	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2253	106	396	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2254	16	397	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2255	44	397	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2256	65	397	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2257	101	397	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2258	16	399	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2259	65	399	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2260	66	400	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2261	104	400	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2262	103	401	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2263	105	401	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2264	106	401	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2265	3	406	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2266	4	406	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2267	6	406	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2268	6	406	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2269	12	406	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2270	20	406	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2271	51	406	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2272	52	406	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2273	53	406	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2274	64	406	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2275	65	406	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2276	81	406	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2277	88	406	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2278	104	406	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2279	105	406	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2280	29	415	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2281	59	415	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2282	60	415	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2283	84	415	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2284	65	416	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2285	39	419	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2286	65	419	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2287	66	419	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2288	101	419	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2289	103	419	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2290	104	419	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2291	106	419	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2292	16	422	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2293	105	422	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2294	44	425	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2295	16	426	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2296	106	426	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2297	66	427	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2298	103	427	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2299	104	427	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2300	105	427	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2301	65	428	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2302	101	428	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2303	62	218	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2304	24	432	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2305	31	432	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2306	34	432	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2307	45	432	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2308	56	432	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2309	63	432	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2310	68	432	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2311	74	432	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2312	75	432	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2313	83	432	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2314	89	432	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2315	91	432	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2316	92	432	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2317	93	432	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2318	97	432	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2319	100	432	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2320	30	433	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2321	32	433	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2322	33	433	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2323	35	433	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2324	57	433	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2325	58	433	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2326	59	433	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2327	67	433	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2328	71	433	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2329	72	433	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2330	73	433	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2331	79	433	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2332	3	434	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2333	4	434	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2334	13	434	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2335	90	434	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2336	91	434	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2337	8	435	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2338	16	435	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2339	28	435	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2340	44	435	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2341	82	435	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2342	16	436	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2343	6	437	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2344	6	437	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2345	10	437	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2346	13	437	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2347	22	437	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2348	24	437	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2349	41	453	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2350	9	454	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2351	42	455	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2352	42	456	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2353	46	456	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2354	61	457	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2355	14	458	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2356	15	459	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2357	109	460	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2358	99	464	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2359	115	465	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2360	63	466	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2361	63	467	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2362	36	468	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2363	41	472	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2364	14	473	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2365	11	437	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2366	16	437	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2367	17	437	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2368	20	437	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2369	37	437	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2370	42	437	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2371	46	437	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2372	49	437	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2373	55	437	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2374	65	437	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2375	69	437	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2376	77	437	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2377	81	437	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2378	88	437	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2379	91	437	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2380	93	437	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2381	99	437	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2382	101	437	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2383	101	437	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2384	103	437	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2385	109	437	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2386	115	437	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2387	48	438	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2388	90	438	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2389	38	439	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2390	39	439	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2391	39	440	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2392	47	441	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2393	11	442	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2394	22	442	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2395	47	443	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2396	109	443	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2397	115	443	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2398	21	444	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2399	21	444	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2400	68	444	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2401	97	444	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2402	103	444	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2403	88	446	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2404	4	447	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2405	4	448	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2406	4	449	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2407	4	450	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2408	4	451	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2409	4	452	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2410	9	452	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2411	500	500	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2412	500	501	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2413	501	502	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2414	502	503	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2415	62	363	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2416	62	419	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2417	62	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2418	62	322	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2419	62	396	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2420	62	426	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2421	62	400	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2422	1012	1115	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2423	1012	1116	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2424	1013	1124	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2425	1013	1125	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2426	1014	1134	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2427	1014	1135	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2428	1015	1143	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2429	1015	1144	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2430	1016	1152	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2431	1016	1153	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2432	1017	1162	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2433	1017	1163	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2434	1018	1171	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2435	1018	1172	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2436	1000	1007	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2437	1000	1008	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2438	503	218	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2439	503	353	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2440	503	363	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2441	503	322	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2442	503	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2443	503	396	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2444	503	419	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2445	503	426	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2446	503	401	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2447	1001	1016	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2448	1001	1017	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2449	1002	1025	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2450	1002	1026	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2451	54	19	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2452	54	21	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2453	54	110	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2454	54	123	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2455	54	165	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2456	54	181	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2457	54	199	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2458	54	212	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2459	54	218	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2460	54	350	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2461	54	353	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2462	54	363	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2463	54	256	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2464	54	257	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2465	54	320	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2466	54	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2467	54	396	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2468	54	400	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2469	54	419	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2470	54	426	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2471	54	322	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2472	1003	1034	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2473	1003	1035	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2474	1	37	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2475	1	55	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2476	1	104	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2477	1	114	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2478	1	135	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2479	1	181	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2480	1	199	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2481	1	245	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2482	1	352	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2483	1	360	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2484	1	261	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2485	1	262	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2486	1	263	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2487	1	334	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2488	1	335	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2489	1	336	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2490	1	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2491	1	432	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2492	1004	1043	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2493	1004	1044	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2494	1005	1052	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2495	1005	1053	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2496	1006	1061	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2497	1006	1062	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2498	1007	1070	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2499	1007	1071	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2500	1008	1079	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2501	1008	1080	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2502	25	15	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2503	25	16	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2504	25	19	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2505	25	20	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2506	25	47	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2507	25	48	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2508	25	81	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2509	25	227	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2510	25	228	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2511	25	301	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2512	25	320	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2513	25	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2514	25	348	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2515	25	350	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2516	25	437	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2517	25	426	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2518	25	396	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2519	25	218	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2520	25	400	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2521	25	363	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2522	25	419	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2523	25	322	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2524	1019	1180	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2525	1019	1181	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2526	603	353	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2527	603	218	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2528	603	363	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2529	603	419	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2530	603	344	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2531	603	322	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2532	603	396	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2533	603	426	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2534	603	399	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2535	1020	1189	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
2536	1020	1190	\N	\N	\N	\N	\N	\N	\N	\N	\N	f
\.


--
-- Data for Name: dish_tags; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.dish_tags (dish_id, tag_id) FROM stdin;
109	13
109	15
115	15
109	16
115	16
109	19
109	21
114	25
115	25
112	32
110	33
112	33
110	34
112	34
110	36
109	41
115	59
109	81
112	81
114	81
115	81
115	101
115	110
113	123
114	155
115	165
115	185
115	187
111	199
112	199
113	199
114	199
109	212
109	217
114	245
115	251
109	256
115	301
115	307
115	315
109	318
109	320
111	320
115	320
115	321
115	328
110	336
108	337
108	344
109	344
110	344
111	344
112	344
113	344
114	344
115	346
115	346
108	348
108	350
108	360
115	360
108	361
115	397
115	400
115	419
115	426
110	432
112	432
113	432
114	432
108	433
96	1
74	2
101	2
101	2
103	2
103	2
106	4
105	11
28	12
5	13
27	13
28	13
56	13
88	13
5	15
10	15
26	15
28	15
40	15
77	15
81	15
82	15
92	15
101	15
101	15
107	15
4	16
5	16
6	16
8	16
10	16
11	16
12	16
13	16
16	16
19	16
20	16
22	16
24	16
26	16
28	16
37	16
38	16
40	16
42	16
44	16
46	16
47	16
49	16
55	16
56	16
61	16
63	16
65	16
66	16
69	16
77	16
78	16
80	16
81	16
82	16
86	16
88	16
91	16
92	16
93	16
95	16
99	16
103	16
105	16
107	16
28	17
2	18
13	18
16	18
44	18
56	18
95	18
96	18
107	18
4	19
6	19
8	19
10	19
12	19
13	19
16	19
18	19
19	19
20	19
22	19
23	19
26	19
37	19
39	19
40	19
42	19
44	19
47	19
48	19
50	19
53	19
1009	1088
55	19
61	19
64	19
65	19
66	19
77	19
78	19
80	19
84	19
86	19
91	19
103	19
105	19
107	19
16	20
45	21
1009	1089
90	21
90	22
6	23
6	23
12	23
17	23
18	23
51	23
60	23
70	23
86	23
90	23
91	23
93	23
103	23
9	24
17	24
38	24
39	24
43	24
48	24
53	24
78	24
82	24
86	24
89	25
101	25
101	25
5	26
17	27
84	27
56	28
5	28
65	29
82	29
5	29
19	30
94	31
94	32
94	34
105	36
1010	1097
29	38
60	38
18	46
23	46
70	46
84	46
85	46
15	47
55	47
64	47
79	47
81	47
89	47
21	48
21	48
91	49
6	49
6	49
80	50
91	50
6	50
6	50
6	51
6	51
23	52
6	52
6	52
10	52
6	53
6	53
34	54
55	54
10	55
51	55
70	55
85	55
1010	1098
10	56
10	57
18	57
40	57
42	57
55	57
103	57
10	58
17	58
40	58
70	58
77	58
93	58
10	59
21	59
21	59
23	59
10	60
55	61
76	61
107	61
9	61
14	62
15	62
23	62
27	62
52	62
64	62
104	62
107	62
31	64
42	64
66	64
105	64
106	64
5	64
27	67
32	67
33	67
57	67
72	67
73	67
89	67
101	67
101	67
107	67
2	69
15	69
30	69
32	69
33	69
57	69
64	69
68	69
72	69
79	69
92	69
96	69
102	69
104	69
106	69
27	71
102	71
106	71
102	72
106	73
40	74
77	74
78	74
102	74
100	75
94	76
100	76
100	77
37	78
57	79
79	79
108	79
108	80
5	81
10	81
26	81
34	81
40	81
45	81
61	81
63	81
66	81
76	81
77	81
82	81
87	81
88	81
103	81
26	82
32	84
105	85
106	85
37	89
96	89
98	90
98	91
98	92
15	95
75	97
18	99
85	99
85	100
4	101
19	101
22	101
41	101
85	101
86	101
15	102
38	102
84	102
85	102
85	103
1011	1106
20	104
23	104
27	104
42	104
48	104
53	104
70	104
85	104
82	105
94	105
94	106
94	107
48	108
48	109
53	109
60	109
48	110
78	110
17	111
48	111
49	112
49	113
80	113
83	113
1011	1107
49	114
74	114
49	115
17	116
17	117
53	117
18	118
18	119
19	120
19	121
19	122
50	122
20	123
62	47
21	124
21	124
21	125
21	125
3	126
22	126
66	126
72	126
22	127
22	128
23	129
52	129
50	130
50	131
53	131
86	131
91	131
50	132
50	133
55	133
50	134
78	134
62	69
50	135
67	142
68	142
72	142
75	142
89	142
92	142
93	142
24	155
31	155
37	155
39	155
39	155
39	155
39	155
39	155
69	155
107	155
93	159
86	161
86	162
86	163
82	164
62	102
61	165
82	165
95	165
38	166
41	166
82	166
61	167
68	167
80	167
27	168
44	168
55	168
65	168
65	169
89	170
89	171
92	172
92	173
79	174
73	175
73	176
73	177
35	178
35	179
62	129
55	181
57	181
67	181
68	181
71	181
73	181
75	181
77	181
79	181
89	181
95	181
100	181
108	181
62	185
14	181
30	181
32	181
33	181
35	181
57	182
68	182
71	182
73	182
108	182
30	182
33	182
35	182
26	183
65	184
8	184
13	184
26	184
44	184
81	185
82	185
26	185
80	186
103	186
26	186
43	186
66	187
81	187
87	187
8	187
44	187
82	188
8	188
44	188
65	189
8	189
66	190
76	190
8	190
72	191
8	191
61	192
68	192
72	192
102	192
106	192
8	192
33	192
43	192
53	193
72	193
8	193
14	193
72	194
79	194
8	194
14	194
34	194
105	198
62	199
57	199
58	199
60	199
63	199
66	199
74	199
80	199
83	199
95	199
99	199
62	204
9	199
14	199
15	199
27	199
31	199
33	199
38	199
41	199
43	199
45	199
43	200
43	201
43	202
52	203
65	203
99	203
100	203
21	203
21	203
43	203
60	204
70	204
84	204
85	204
18	204
23	204
29	204
29	205
29	206
92	206
2	206
29	207
20	208
29	208
70	208
31	209
27	210
31	210
38	210
83	210
107	210
31	211
83	211
100	211
4	212
9	212
62	238
66	212
75	212
76	212
80	212
75	213
16	218
66	219
104	219
66	227
97	227
101	227
101	227
14	229
35	229
68	229
71	229
81	230
81	231
81	232
40	233
40	234
9	237
59	237
13	238
41	238
96	238
79	242
80	242
88	242
14	243
57	243
80	243
24	244
20	245
48	245
66	245
86	245
107	245
62	255
10	245
42	247
51	247
64	247
70	247
76	247
82	247
91	247
92	247
93	247
51	248
52	249
60	249
52	250
9	251
61	251
76	251
77	251
99	251
77	252
13	255
80	255
4	256
9	256
62	284
62	293
55	258
55	259
56	260
62	320
62	344
62	347
88	263
104	264
2	267
30	267
32	267
33	267
59	267
71	267
68	268
75	268
89	268
92	268
42	271
58	271
58	272
79	272
58	273
30	274
95	278
95	279
95	282
13	284
66	284
66	285
46	286
66	286
88	286
42	287
66	287
3	288
76	289
30	293
30	294
59	294
71	294
59	295
79	296
79	297
10	301
61	301
66	301
69	301
76	301
80	301
82	301
95	301
95	301
17	303
43	303
50	303
53	303
86	303
76	308
76	309
14	310
42	310
76	310
13	311
13	312
15	315
36	315
77	315
85	315
39	316
44	317
101	317
101	317
2	320
3	320
4	320
5	320
6	320
7	320
8	320
9	320
10	320
11	320
12	320
13	320
14	320
15	320
16	320
17	320
18	320
19	320
20	320
21	320
21	320
22	320
23	320
26	320
27	320
28	320
29	320
36	320
37	320
38	320
39	320
40	320
41	320
42	320
43	320
44	320
46	320
47	320
48	320
49	320
50	320
50	320
51	320
52	320
53	320
62	350
55	320
56	320
60	320
61	320
63	320
64	320
65	320
66	320
69	320
70	320
76	320
77	320
78	320
80	320
81	320
82	320
84	320
85	320
86	320
88	320
90	320
91	320
94	320
95	320
96	320
98	320
99	320
101	320
101	320
103	320
104	320
105	320
106	320
107	320
39	321
66	321
103	321
106	321
65	322
105	322
5	323
26	323
47	323
61	323
65	323
76	323
77	323
80	323
81	323
82	323
99	323
101	323
101	323
16	324
76	324
104	324
77	325
16	327
39	327
104	327
103	328
106	328
39	330
62	360
12	334
15	334
28	334
42	334
43	334
51	334
52	334
53	334
55	334
56	334
60	334
61	334
63	334
64	334
65	334
68	334
70	334
76	334
77	334
80	334
82	334
86	334
91	334
92	334
97	334
99	334
100	334
102	334
103	334
62	460
4	335
8	335
9	335
38	335
39	335
56	335
62	461
3	336
4	336
9	336
12	336
15	336
18	336
19	336
23	336
29	336
31	336
38	336
39	336
42	336
47	336
48	336
52	336
55	336
61	336
63	336
74	336
83	336
85	336
91	336
28	337
39	337
8	338
47	338
65	338
2	340
5	340
46	340
47	340
47	341
55	341
77	341
86	341
42	342
49	342
69	342
87	342
8	343
26	343
65	343
69	343
62	462
2	344
3	344
4	344
6	344
7	344
8	344
9	344
10	344
11	344
12	344
13	344
14	344
16	344
17	344
18	344
19	344
20	344
21	344
22	344
23	344
24	344
26	344
27	344
28	344
30	344
32	344
33	344
34	344
35	344
37	344
38	344
39	344
40	344
41	344
43	344
44	344
45	344
47	344
48	344
49	344
50	344
51	344
52	344
53	344
62	463
55	344
56	344
57	344
58	344
61	344
63	344
64	344
65	344
67	344
68	344
69	344
70	344
71	344
72	344
73	344
75	344
76	344
78	344
79	344
80	344
81	344
82	344
83	344
86	344
87	344
88	344
89	344
90	344
91	344
92	344
93	344
94	344
95	344
96	344
97	344
98	344
99	344
100	344
102	344
104	344
107	344
69	345
2	346
3	346
4	346
8	346
9	346
11	346
13	346
42	346
44	346
55	346
61	346
64	346
76	346
78	346
88	346
91	346
96	346
99	346
104	346
105	346
11	347
13	347
14	347
20	347
21	347
21	347
22	347
30	347
49	347
55	347
64	347
65	347
66	347
70	347
76	347
84	347
88	347
93	347
103	347
105	347
3	348
6	348
6	348
11	348
15	348
17	348
20	348
21	348
21	348
27	348
32	348
34	348
35	348
40	348
40	348
51	348
53	348
57	348
59	348
60	348
61	348
64	348
67	348
68	348
70	348
71	348
73	348
76	348
79	348
84	348
89	348
92	348
93	348
95	348
97	348
101	348
101	348
104	348
107	348
11	349
51	349
107	349
11	350
13	350
27	350
30	350
32	350
35	350
62	464
57	350
61	350
64	350
67	350
68	350
70	350
71	350
76	350
79	350
89	350
93	350
99	350
104	350
107	350
11	351
47	351
55	351
62	465
3	352
11	352
23	352
29	352
52	352
8	353
13	353
18	353
19	353
44	353
47	353
49	353
65	353
80	353
82	353
86	353
6	354
6	354
10	354
12	354
47	354
53	354
63	354
92	354
8	355
12	355
66	355
80	355
91	355
12	356
38	356
94	356
12	357
15	357
68	357
84	357
8	358
12	358
72	358
75	358
79	358
12	359
79	359
62	353
8	360
12	360
15	360
30	360
32	360
42	360
51	360
52	360
53	360
55	360
56	360
57	360
60	360
61	360
63	360
64	360
66	360
67	360
68	360
70	360
71	360
72	360
75	360
76	360
77	360
80	360
88	360
89	360
91	360
92	360
97	360
99	360
100	360
102	360
103	360
58	361
68	361
72	361
76	361
80	361
44	363
65	363
105	363
66	364
42	368
64	368
99	368
104	395
39	396
66	396
103	396
105	396
106	396
16	397
44	397
65	397
101	397
16	399
65	399
66	400
104	400
103	401
105	401
106	401
3	406
4	406
6	406
6	406
12	406
20	406
51	406
52	406
53	406
64	406
65	406
81	406
88	406
104	406
105	406
29	415
59	415
60	415
84	415
65	416
39	419
65	419
66	419
101	419
103	419
104	419
106	419
16	422
105	422
44	425
16	426
106	426
66	427
103	427
104	427
105	427
65	428
101	428
62	218
24	432
31	432
34	432
45	432
56	432
63	432
68	432
74	432
75	432
83	432
89	432
91	432
92	432
93	432
97	432
100	432
30	433
32	433
33	433
35	433
57	433
58	433
59	433
67	433
71	433
72	433
73	433
79	433
3	434
4	434
13	434
90	434
91	434
8	435
16	435
28	435
44	435
82	435
16	436
6	437
6	437
10	437
13	437
22	437
24	437
41	453
9	454
42	455
42	456
46	456
61	457
14	458
15	459
109	460
99	464
115	465
63	466
63	467
36	468
41	472
14	473
11	437
16	437
17	437
20	437
37	437
42	437
46	437
49	437
55	437
65	437
69	437
77	437
81	437
88	437
91	437
93	437
99	437
101	437
101	437
103	437
109	437
115	437
48	438
90	438
38	439
39	439
39	440
47	441
11	442
22	442
47	443
109	443
115	443
21	444
21	444
68	444
97	444
103	444
88	446
4	447
4	448
4	449
4	450
4	451
4	452
9	452
500	500
500	501
501	502
502	503
62	363
62	419
62	344
62	322
62	396
62	426
62	400
1012	1115
1012	1116
1013	1124
1013	1125
1014	1134
1014	1135
1015	1143
1015	1144
1016	1152
1016	1153
1017	1162
1017	1163
1018	1171
1018	1172
1000	1007
1000	1008
503	218
503	353
503	363
503	322
503	344
503	396
503	419
503	426
503	401
1001	1016
1001	1017
1002	1025
1002	1026
54	19
54	21
54	110
54	123
54	165
54	181
54	199
54	212
54	218
54	350
54	353
54	363
54	256
54	257
54	320
54	344
54	396
54	400
54	419
54	426
54	322
1003	1034
1003	1035
1	37
1	55
1	104
1	114
1	135
1	181
1	199
1	245
1	352
1	360
1	261
1	262
1	263
1	334
1	335
1	336
1	344
1	432
1004	1043
1004	1044
1005	1052
1005	1053
1006	1061
1006	1062
1007	1070
1007	1071
1008	1079
1008	1080
25	15
25	16
25	19
25	20
25	47
25	48
25	81
25	227
25	228
25	301
25	320
25	344
25	348
25	350
25	437
25	426
25	396
25	218
25	400
25	363
25	419
25	322
1019	1180
1019	1181
603	353
603	218
603	363
603	419
603	344
603	322
603	396
603	426
603	399
1020	1189
1020	1190
\.


--
-- Data for Name: domain_unit; Type: TABLE DATA; Schema: public; Owner: bankuser
--

COPY public.domain_unit (domain_unit_id, domain_type, unit_id) FROM stdin;
1000	METRIC	1000
1001	METRIC	1001
1002	METRIC	1002
1003	METRIC	1003
1004	METRIC	1004
1005	METRIC	1011
1006	METRIC	1013
1007	METRIC	1014
1008	METRIC	1015
1009	METRIC	1016
1010	METRIC	1022
1011	METRIC	1023
1012	METRIC	1029
1013	METRIC	1030
1014	METRIC	1031
1015	METRIC	1037
1016	METRIC	1038
1017	METRIC	1039
1018	METRIC	1040
1019	METRIC	1041
1020	METRIC	1042
1021	METRIC	1043
1022	METRIC	1044
1023	METRIC	1045
1024	METRIC	1046
1025	METRIC	1047
1026	METRIC	1048
1027	METRIC	1049
1028	METRIC	1050
1029	US	1000
1030	US	1001
1031	US	1002
1032	US	1005
1033	US	1006
1034	US	1007
1035	US	1008
1036	US	1009
1037	US	1010
1038	US	1011
1039	US	1017
1040	US	1019
1041	US	1021
1042	US	1022
1043	US	1023
1044	US	1029
1045	US	1030
1046	US	1031
1047	US	1032
1048	US	1033
1049	US	1034
1050	US	1035
1051	US	1036
1052	US	1037
1053	US	1038
1054	US	1039
1055	US	1040
1056	US	1041
1057	US	1042
1058	US	1043
1059	US	1044
1060	US	1045
1061	US	1046
1062	US	1047
1063	US	1048
1064	US	1049
1065	US	1050
1066	UK	1024
1067	UK	1025
1068	UK	1026
1069	UK	1027
1070	UK	1028
1071	UK	1051
1072	UK	1052
1073	UK	1013
1074	UK	1014
1075	UK	1016
1076	UK	1000
1077	UK	1001
1078	UK	1002
1079	UK	1011
1080	UK	1022
1081	UK	1023
1082	UK	1029
1083	UK	1030
1084	UK	1031
1085	UK	1037
1086	UK	1038
1087	UK	1039
1088	UK	1040
1089	UK	1041
1090	UK	1042
1091	UK	1043
1092	UK	1044
1093	UK	1045
1094	UK	1046
1095	UK	1047
1096	UK	1048
1097	UK	1049
1098	UK	1050
\.


--
-- Data for Name: units; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.units (unit_id, type, subtype, name, is_liquid, is_list_unit, is_dish_unit, is_weight, is_volume, is_tag_specific, excluded_domains, one_way_conversion) FROM stdin;
1000	HYBRID	SOLID	cup	f	f	t	f	f	f	METRIC	f
1001	HYBRID	SOLID	tablespoon	f	f	t	f	f	f		f
1002	HYBRID	SOLID	teaspoon	f	f	t	f	f	f		f
1003	METRIC	VOLUME	liter	t	t	t	f	f	f		f
1004	METRIC	VOLUME	milliliter	t	t	t	f	f	f		f
1005	US	VOLUME	gallon	t	t	t	f	f	f		f
1006	US	VOLUME	pint	t	f	t	f	f	f		f
1007	US	VOLUME	fl oz	t	t	t	f	f	f		f
1008	US	WEIGHT	lb	f	t	t	f	f	f		f
1009	US	WEIGHT	oz	f	t	t	f	f	f		f
1010	US	VOLUME	quart	t	t	t	f	f	f		f
1011	UNIT	NONE	unit	f	t	t	f	f	f		f
1013	METRIC	WEIGHT	gram	f	t	t	f	f	f		f
1014	METRIC	WEIGHT	kilogram	f	t	t	f	f	f		f
1015	METRIC	VOLUME	centiliter	t	f	t	f	f	f		f
1016	METRIC	WEIGHT	milligram	f	t	t	f	f	f		f
1017	US	VOLUME	cup (fluid)	t	f	t	f	f	f		f
1019	US	LIQUID	teaspoon (fluid)	t	f	t	f	f	f		f
1021	US	LIQUID	tablespoon (fluid)	t	f	t	f	f	f		f
1022	HYBRID	SOLID	slice	f	f	t	f	f	f		f
1023	HYBRID	SOLID	stick	f	f	t	f	f	f		f
1024	UK	VOLUME	gallon (UK)	t	t	t	f	f	f		f
1025	UK	VOLUME	pint (UK)	t	f	t	f	f	f		f
1026	UK	VOLUME	fl oz (UK)	t	t	t	f	f	f		f
1027	UK	VOLUME	quart (UK)	t	t	t	f	f	f		f
1028	UK	VOLUME	cup (fluid) (UK)	t	f	t	f	f	f		f
1029	HYBRID	VOLUME	can	f	t	t	f	f	f		t
1030	HYBRID	VOLUME	large can	f	t	t	f	f	f		t
1031	HYBRID	VOLUME	small can	f	t	t	f	f	f		t
1032	US	VOLUME	#2 can	f	f	t	f	f	f		f
1033	US	VOLUME	14.5 oz can	f	f	t	f	f	f		f
1034	US	VOLUME	#2.5 can	f	f	t	f	f	f		f
1035	US	VOLUME	#3 can	f	f	t	f	f	f		f
1036	US	VOLUME	29 oz can	f	f	t	f	f	f		f
1037	HYBRID	WEIGHT	bulb	f	f	t	f	f	t		f
1038	HYBRID	WEIGHT	ear	f	t	t	f	f	t		f
1039	HYBRID	WEIGHT	head	f	t	t	f	f	t		f
1040	HYBRID	WEIGHT	leaf	f	f	t	f	f	t		f
1041	HYBRID	WEIGHT	package	f	t	t	f	f	t		f
1042	HYBRID	WEIGHT	packet	f	t	t	f	f	t		f
1043	HYBRID	WEIGHT	pod	f	f	t	f	f	t		f
1044	HYBRID	WEIGHT	ring	f	f	t	f	f	t		f
1045	HYBRID	WEIGHT	sheet	f	f	t	f	f	t		f
1046	HYBRID	WEIGHT	spear	f	f	t	f	f	t		f
1047	HYBRID	WEIGHT	sprig	f	f	t	f	f	t		f
1048	HYBRID	WEIGHT	stalk	f	f	t	f	f	t		f
1049	HYBRID	WEIGHT	butter stick	f	f	t	f	f	t	METRIC,UK	f
1050	HYBRID	WEIGHT	wedge	f	f	t	f	f	t		f
1051	UK	VOLUME	teaspoon (fluid) (UK)	t	f	t	f	f	f		f
1052	UK	VOLUME	tablespoon (fluid) (UK)	t	f	t	f	f	f		f
\.


--
-- Data for Name: factors; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.factors (factor_id, factor, to_unit, from_unit, conversion_id, reference_id, marker, unit_size, unit_default) FROM stdin;
1	0.0625	1005	1017	\N	\N	\N	\N	\N
2	0.5	1006	1017	\N	\N	\N	\N	\N
3	0.25	1010	1017	\N	\N	\N	\N	\N
4	0.125	1017	1007	\N	\N	\N	\N	\N
5	0.0078125	1005	1007	\N	\N	\N	\N	\N
6	0.0625	1006	1007	\N	\N	\N	\N	\N
7	0.03125	1010	1007	\N	\N	\N	\N	\N
8	0.00100000000000000002	1014	1013	\N	\N	\N	\N	\N
9	0.00220000000000000013	1008	1013	\N	\N	\N	\N	\N
10	0.0352733686067019034	1009	1013	\N	\N	\N	\N	\N
11	2.20462261999999987	1008	1014	\N	\N	\N	\N	\N
12	35.2733686067019008	1009	1014	\N	\N	\N	\N	\N
13	100	1015	1003	\N	\N	\N	\N	\N
14	4.22675000000000001	1017	1003	\N	\N	\N	\N	\N
15	0.264170000000000016	1005	1003	\N	\N	\N	\N	\N
16	1000	1004	1003	\N	\N	\N	\N	\N
17	2.11337639999999993	1006	1003	\N	\N	\N	\N	\N
18	1.05668820999999991	1010	1003	\N	\N	\N	\N	\N
19	0.0042267499999999996	1017	1004	\N	\N	\N	\N	\N
20	0.000264169999999999986	1005	1004	\N	\N	\N	\N	\N
21	0.00211337639999999985	1006	1004	\N	\N	\N	\N	\N
22	0.00105668819999999992	1010	1004	\N	\N	\N	\N	\N
23	0.125	1005	1006	\N	\N	\N	\N	\N
24	0.5	1010	1006	\N	\N	\N	\N	\N
25	0.25	1005	1010	\N	\N	\N	\N	\N
26	2.20000000000000011e-06	1008	1016	\N	\N	\N	\N	\N
27	3.52699999999999994e-05	1009	1016	\N	\N	\N	\N	\N
28	0.0105668820999999993	1010	1015	\N	\N	\N	\N	\N
29	0.0422674999999999995	1017	1015	\N	\N	\N	\N	\N
30	0.00264172051241560015	1005	1015	\N	\N	\N	\N	\N
31	0.0211337639999999993	1006	1015	\N	\N	\N	\N	\N
32	0.00100000000000000002	1003	1004	\N	\N	\N	\N	\N
33	0.100000000000000006	1015	1004	\N	\N	\N	\N	\N
34	0.0100000000000000002	1003	1015	\N	\N	\N	\N	\N
35	10	1004	1015	\N	\N	\N	\N	\N
36	0.0625	1008	1009	\N	\N	\N	\N	\N
37	16	1009	1008	\N	\N	\N	\N	\N
38	0.333333333299999979	1021	1019	\N	\N	\N	\N	\N
39	0.333333333329999981	1001	1002	\N	\N	\N	\N	\N
40	0.0208333333330000016	1000	1002	\N	\N	\N	\N	\N
41	0.0208333333330000016	1017	1019	\N	\N	\N	\N	\N
42	0.0208333333330000016	1000	1002	\N	\N	\N	\N	\N
43	0.0625	1000	1001	\N	\N	\N	\N	\N
44	0.0625	1017	1021	\N	\N	\N	\N	\N
45	3	1002	1001	\N	\N	\N	\N	\N
46	3	1019	1021	\N	\N	\N	\N	\N
47	0.00390625	1005	1021	\N	\N	\N	\N	\N
48	0.03125	1006	1021	\N	\N	\N	\N	\N
49	0.5	1007	1021	\N	\N	\N	\N	\N
50	0.015625	1010	1021	\N	\N	\N	\N	\N
51	0.00130208333000000009	1005	1019	\N	\N	\N	\N	\N
52	0.0104166666666000007	1006	1019	\N	\N	\N	\N	\N
53	0.166666666600000013	1007	1019	\N	\N	\N	\N	\N
54	0.00520833333332999977	1010	1019	\N	\N	\N	\N	\N
55	128	1007	1005	\N	\N	\N	\N	\N
56	16	1007	1006	\N	\N	\N	\N	\N
57	8	1007	1017	\N	\N	\N	\N	\N
58	33.8140227000000024	1007	1003	\N	\N	\N	\N	\N
59	0.0338140229999999986	1007	1004	\N	\N	\N	\N	\N
60	0.338142029999999982	1007	1015	\N	\N	\N	\N	\N
61	1000	1013	1014	\N	\N	\N	\N	\N
62	16	1001	1000	\N	\N	\N	\N	\N
63	48	1002	1000	\N	\N	\N	\N	\N
64	3.51950797278540017	1028	1003	\N	\N	\N	\N	\N
65	0.219969248299090009	1024	1003	\N	\N	\N	\N	\N
66	1.75975398639270009	1025	1003	\N	\N	\N	\N	\N
67	0.879876993196350043	1027	1003	\N	\N	\N	\N	\N
68	0.00021996924829909001	1024	1004	\N	\N	\N	\N	\N
69	0.00175975398639269991	1025	1004	\N	\N	\N	\N	\N
70	0.000879876993196349955	1027	1004	\N	\N	\N	\N	\N
71	0.125	1024	1025	\N	\N	\N	\N	\N
72	0.5	1025	1028	\N	\N	\N	\N	\N
73	0.25	1024	1027	\N	\N	\N	\N	\N
74	0.00879876993196350085	1027	1015	\N	\N	\N	\N	\N
75	0.0351950797278540034	1028	1015	\N	\N	\N	\N	\N
76	0.00219969248299089993	1024	1015	\N	\N	\N	\N	\N
77	0.0175975398639270017	1025	1015	\N	\N	\N	\N	\N
78	0.0208333333333329991	1028	1051	\N	\N	\N	\N	\N
79	0.0625	1028	1052	\N	\N	\N	\N	\N
80	0.00312500000000000017	1024	1052	\N	\N	\N	\N	\N
81	0.0250000099999999996	1025	1052	\N	\N	\N	\N	\N
82	0.625	1026	1052	\N	\N	\N	\N	\N
83	0.0125000000000000007	1027	1052	\N	\N	\N	\N	\N
84	0.00104166999999999997	1024	1051	\N	\N	\N	\N	\N
85	0.00833333999999999984	1025	1051	\N	\N	\N	\N	\N
86	0.20833299999999999	1026	1051	\N	\N	\N	\N	\N
87	0.00520833333332999977	1027	1051	\N	\N	\N	\N	\N
88	160	1026	1024	\N	\N	\N	\N	\N
89	20	1026	1025	\N	\N	\N	\N	\N
90	10	1026	1028	\N	\N	\N	\N	\N
91	35.1950797278540009	1026	1003	\N	\N	\N	\N	\N
92	0.0351950797278540034	1026	1004	\N	\N	\N	\N	\N
93	0.351950797278540006	1026	1015	\N	\N	\N	\N	\N
94	56.3121275645659978	1052	1003	\N	\N	\N	\N	\N
95	0.563121275645659947	1052	1015	\N	\N	\N	\N	\N
96	0.0563121275645670008	1052	1004	\N	\N	\N	\N	\N
97	168.936382693700011	1051	1003	\N	\N	\N	\N	\N
98	1.68936382693699993	1051	1015	\N	\N	\N	\N	\N
99	0.16893638269370001	1051	1004	\N	\N	\N	\N	\N
100	0.00351950797278539982	1028	1004	\N	\N	\N	\N	\N
101	0.0625	1024	1028	\N	\N	\N	\N	\N
102	0.25	1027	1028	\N	\N	\N	\N	\N
103	1	1029	1032	\N	\N	\N	\N	\N
104	1	1029	1033	\N	\N	\N	\N	\N
105	1	1030	1034	\N	\N	\N	\N	\N
106	1	1030	1035	\N	\N	\N	\N	\N
107	1	1030	1036	\N	\N	\N	\N	\N
\.



--
-- Data for Name: food_categories; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.food_categories (category_id, category_code, name) FROM stdin;
\.


--
-- Data for Name: food_category_mapping; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.food_category_mapping (food_category_mapping_id, category_id, tag_id) FROM stdin;
\.


--
-- Data for Name: food_conversions; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.food_conversions (conversion_id, food_id, fdc_id, amount, unit_name, gram_weight, unit_id, food_conversion_id, integral, marker, sub_amount, info, unit_size, unit_default) FROM stdin;
\.


--
-- Data for Name: foods; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.foods (food_id, fdc_id, name, category_id, marker, has_factor, conversion_id, original_name, integral) FROM stdin;
\.


--
-- Data for Name: list; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.list (list_id, created_on, user_id, list_types, list_layout_id, last_update, meal_plan_id, is_starter_list, name) FROM stdin;
500	2021-04-11 09:46:31.681524+00	500	BaseList	\N	\N	\N	f	list3
501	2021-04-11 09:46:31.681524+00	20	ActiveList	\N	\N	\N	f	list2
502	2021-04-11 09:46:31.681524+00	500	ActiveList	\N	\N	\N	f	list1
402	2021-04-11 09:46:31.681524+00	20	ActiveList	\N	\N	\N	f	list1
\.


--
-- Data for Name: list_item; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.list_item (item_id, added_on, crossed_off, free_text, source, list_id, list_category, tag_id, used_count, category_id, dish_sources, list_sources, removed_on, updated_on) FROM stdin;
500	2021-04-11 09:46:31.681524+00	\N	\N	\N	500	\N	501	1	\N	\N	\N	\N	\N
501	2021-04-11 09:46:31.681524+00	\N	\N	\N	500	\N	502	1	\N	\N	\N	\N	\N
502	2021-04-11 09:46:31.681524+00	\N	\N	\N	500	\N	503	1	\N	\N	\N	\N	\N
503	2021-04-11 09:46:31.681524+00	\N	\N	\N	500	\N	500	1	\N	\N	\N	\N	\N
504	2021-04-11 09:46:31.681524+00	\N	\N	\N	500	\N	504	1	\N	\N	\N	\N	\N
505	2021-04-11 09:46:31.681524+00	\N	\N	\N	501	\N	16	1	\N	16;90	\N	\N	\N
506	2021-04-11 09:46:31.681524+00	\N	\N	\N	501	\N	18	1	\N	54;55	\N	\N	\N
507	2021-04-11 09:46:31.681524+00	\N	\N	\N	501	\N	21	1	\N	90	\N	\N	\N
508	2021-04-11 09:46:31.681524+00	\N	\N	\N	501	\N	359	1	\N	55;56	501	\N	\N
509	2021-04-11 09:46:31.681524+00	\N	\N	\N	501	\N	470	1	\N	\N	402	\N	\N
510	2021-04-11 09:46:31.681524+00	\N	\N	\N	501	\N	210	1	\N	\N	402	\N	\N
511	2021-04-11 09:46:31.681524+00	\N	\N	\N	501	\N	211	1	\N	\N	402	\N	\N
512	2021-04-11 09:46:31.681524+00	\N	\N	\N	501	\N	113	2	\N	;83;	\N	\N	\N
\.


--
-- Data for Name: list_stat_configs; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.list_stat_configs (added_dish_factor, added_single_factor, added_list_factor, added_starterlist_factor, removed_dish_factor, removed_single_factor, removed_list_factor, removed_starterlist_factor, frequent_threshold) FROM stdin;
25	25	25	25	20	40	20	20	0.800000000000000044
\.


--
-- Data for Name: list_tag_stats; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.list_tag_stats (list_tag_stat_id, added_count, removed_count, tag_id, user_id, added_to_dish, added_single, added_dish, added_list, added_starterlist, removed_single, removed_dish, removed_list, removed_starterlist) FROM stdin;
1	1	1	1	20	0	0	0	0	0	0	0	0	0
2	3	3	12	1	0	0	0	0	0	0	0	0	0
3	42	31	13	1	0	0	0	0	0	0	0	0	0
4	47	34	15	1	0	0	0	0	0	0	0	0	0
5	64	47	16	1	0	0	0	0	0	0	0	0	0
6	3	3	17	1	0	0	0	0	0	0	0	0	0
7	8	0	18	20	0	0	0	0	0	0	0	0	0
8	48	32	19	1	0	0	0	0	0	0	0	0	0
9	6	2	20	20	0	0	0	0	0	0	0	0	0
10	37	28	20	1	0	0	0	0	0	0	0	0	0
11	11	0	21	20	0	0	0	0	0	0	0	0	0
12	3	3	21	1	0	0	0	0	0	0	0	0	0
13	2	0	22	20	0	0	0	0	0	0	0	0	0
14	3	3	22	1	0	0	0	0	0	0	0	0	0
15	46	31	23	1	0	0	0	0	0	0	0	0	0
16	42	30	25	1	0	0	0	0	0	0	0	0	0
17	16	6	25	20	0	0	0	0	0	0	0	0	0
18	39	28	26	1	0	0	0	0	0	0	0	0	0
19	1	0	27	1	0	0	0	0	0	0	0	0	0
20	1	0	27	20	0	0	0	0	0	0	0	0	0
21	1	0	28	20	0	0	0	0	0	0	0	0	0
22	39	28	28	1	0	0	0	0	0	0	0	0	0
23	2	2	29	20	0	0	0	0	0	0	0	0	0
24	41	27	29	1	0	0	0	0	0	0	0	0	0
25	1	0	31	20	0	0	0	0	0	0	0	0	0
26	1	0	31	1	0	0	0	0	0	0	0	0	0
27	10	0	32	20	0	0	0	0	0	0	0	0	0
28	42	29	32	1	0	0	0	0	0	0	0	0	0
29	26	2	65	20	0	0	0	0	0	0	0	0	0
30	56	46	66	1	0	0	0	0	0	0	0	0	0
31	63	46	67	1	0	0	0	0	0	0	0	0	0
32	63	45	68	1	0	0	0	0	0	0	0	0	0
33	2	0	76	20	0	0	0	0	0	0	0	0	0
34	40	28	76	1	0	0	0	0	0	0	0	0	0
35	39	27	77	1	0	0	0	0	0	0	0	0	0
36	1	1	77	20	0	0	0	0	0	0	0	0	0
37	4	2	78	20	0	0	0	0	0	0	0	0	0
38	39	28	78	1	0	0	0	0	0	0	0	0	0
39	4	2	79	20	0	0	0	0	0	0	0	0	0
40	2	1	79	1	0	0	0	0	0	0	0	0	0
41	2	0	80	20	0	0	0	0	0	0	0	0	0
42	2	1	80	1	0	0	0	0	0	0	0	0	0
43	64	50	81	1	0	0	0	0	0	0	0	0	0
44	27	3	81	20	0	0	0	0	0	0	0	0	0
45	1	0	82	20	0	0	0	0	0	0	0	0	0
46	2	1	82	1	0	0	0	0	0	0	0	0	0
47	1	0	84	20	0	0	0	0	0	0	0	0	0
48	2	2	85	20	0	0	0	0	0	0	0	0	0
49	4	0	89	20	0	0	0	0	0	0	0	0	0
50	39	28	89	1	0	0	0	0	0	0	0	0	0
51	6	3	90	20	0	0	0	0	0	0	0	0	0
52	39	28	90	1	0	0	0	0	0	0	0	0	0
53	6	0	91	20	0	0	0	0	0	0	0	0	0
54	39	27	91	1	0	0	0	0	0	0	0	0	0
55	6	0	92	20	0	0	0	0	0	0	0	0	0
56	39	28	92	1	0	0	0	0	0	0	0	0	0
57	0	0	93	1	0	0	0	0	0	0	0	0	0
58	0	0	94	1	0	0	0	0	0	0	0	0	0
59	24	4	95	20	0	0	0	0	0	0	0	0	0
60	63	47	95	1	0	0	0	0	0	0	0	0	0
61	26	8	96	20	0	0	0	0	0	0	0	0	0
62	63	48	96	1	0	0	0	0	0	0	0	0	0
63	1	0	97	20	0	0	0	0	0	0	0	0	0
64	63	46	97	1	0	0	0	0	0	0	0	0	0
65	63	46	98	1	0	0	0	0	0	0	0	0	0
66	25	2	98	20	0	0	0	0	0	0	0	0	0
67	1	0	99	20	0	0	0	0	0	0	0	0	0
68	2	0	99	1	0	0	0	0	0	0	0	0	0
69	1	0	100	20	0	0	0	0	0	0	0	0	0
70	5	0	101	20	0	0	0	0	0	0	0	0	0
71	3	0	102	20	0	0	0	0	0	0	0	0	0
72	1	0	102	1	0	0	0	0	0	0	0	0	0
73	1	0	103	20	0	0	0	0	0	0	0	0	0
74	4	0	104	20	0	0	0	0	0	0	0	0	0
75	2	1	105	20	0	0	0	0	0	0	0	0	0
76	2	0	105	1	0	0	0	0	0	0	0	0	0
77	1	0	3	20	0	0	0	0	0	0	0	0	0
78	10	0	23	20	0	0	0	0	0	0	0	0	0
79	10	0	33	20	0	0	0	0	0	0	0	0	0
80	41	28	33	1	0	0	0	0	0	0	0	0	0
81	22	5	34	20	0	0	0	0	0	0	0	0	0
82	63	47	34	1	0	0	0	0	0	0	0	0	0
83	5	0	36	20	0	0	0	0	0	0	0	0	0
84	1	0	37	20	0	0	0	0	0	0	0	0	0
85	2	0	38	20	0	0	0	0	0	0	0	0	0
86	1	0	40	20	0	0	0	0	0	0	0	0	0
87	4	2	41	20	0	0	0	0	0	0	0	0	0
88	2	0	42	1	0	0	0	0	0	0	0	0	0
89	20	14	42	20	0	0	0	0	0	0	0	0	0
90	2	0	106	1	0	0	0	0	0	0	0	0	0
91	1	0	106	20	0	0	0	0	0	0	0	0	0
92	1	0	107	20	0	0	0	0	0	0	0	0	0
93	1	0	107	1	0	0	0	0	0	0	0	0	0
94	3	1	110	20	0	0	0	0	0	0	0	0	0
95	5	0	113	20	0	0	0	0	0	0	0	0	0
96	1	1	114	20	0	0	0	0	0	0	0	0	0
97	2	0	118	1	0	0	0	0	0	0	0	0	0
98	2	0	119	1	0	0	0	0	0	0	0	0	0
99	2	0	122	1	0	0	0	0	0	0	0	0	0
100	5	2	123	20	0	0	0	0	0	0	0	0	0
101	3	3	126	20	0	0	0	0	0	0	0	0	0
102	2	0	130	1	0	0	0	0	0	0	0	0	0
103	2	0	131	1	0	0	0	0	0	0	0	0	0
104	6	0	131	20	0	0	0	0	0	0	0	0	0
105	2	0	132	1	0	0	0	0	0	0	0	0	0
106	2	0	133	1	0	0	0	0	0	0	0	0	0
107	2	0	134	1	0	0	0	0	0	0	0	0	0
108	2	0	134	20	0	0	0	0	0	0	0	0	0
109	2	0	135	1	0	0	0	0	0	0	0	0	0
110	24	4	138	20	0	0	0	0	0	0	0	0	0
111	25	9	139	20	0	0	0	0	0	0	0	0	0
112	22	10	140	20	0	0	0	0	0	0	0	0	0
113	4	4	141	20	0	0	0	0	0	0	0	0	0
114	1	0	159	20	0	0	0	0	0	0	0	0	0
115	3	0	161	20	0	0	0	0	0	0	0	0	0
116	3	0	162	20	0	0	0	0	0	0	0	0	0
117	5	2	163	20	0	0	0	0	0	0	0	0	0
118	1	0	164	20	0	0	0	0	0	0	0	0	0
119	2	0	164	1	0	0	0	0	0	0	0	0	0
120	3	2	165	20	0	0	0	0	0	0	0	0	0
121	2	0	165	1	0	0	0	0	0	0	0	0	0
122	3	0	166	20	0	0	0	0	0	0	0	0	0
123	2	0	166	1	0	0	0	0	0	0	0	0	0
124	5	1	167	20	0	0	0	0	0	0	0	0	0
125	6	5	168	20	0	0	0	0	0	0	0	0	0
126	1	0	168	1	0	0	0	0	0	0	0	0	0
127	2	1	169	20	0	0	0	0	0	0	0	0	0
128	1	0	169	1	0	0	0	0	0	0	0	0	0
129	1	0	170	20	0	0	0	0	0	0	0	0	0
130	1	1	171	20	0	0	0	0	0	0	0	0	0
131	1	0	172	20	0	0	0	0	0	0	0	0	0
132	1	1	173	20	0	0	0	0	0	0	0	0	0
133	2	1	174	20	0	0	0	0	0	0	0	0	0
134	2	0	175	20	0	0	0	0	0	0	0	0	0
135	2	0	176	20	0	0	0	0	0	0	0	0	0
136	2	1	177	20	0	0	0	0	0	0	0	0	0
137	1	0	183	20	0	0	0	0	0	0	0	0	0
138	2	1	183	1	0	0	0	0	0	0	0	0	0
139	4	1	184	1	0	0	0	0	0	0	0	0	0
140	7	6	184	20	0	0	0	0	0	0	0	0	0
141	7	4	185	20	0	0	0	0	0	0	0	0	0
142	5	1	185	1	0	0	0	0	0	0	0	0	0
143	6	3	186	20	0	0	0	0	0	0	0	0	0
144	2	1	186	1	0	0	0	0	0	0	0	0	0
145	2	0	187	1	0	0	0	0	0	0	0	0	0
146	11	0	187	20	0	0	0	0	0	0	0	0	0
147	4	0	188	20	0	0	0	0	0	0	0	0	0
148	3	0	188	1	0	0	0	0	0	0	0	0	0
149	2	0	189	1	0	0	0	0	0	0	0	0	0
150	2	0	189	20	0	0	0	0	0	0	0	0	0
151	2	0	190	20	0	0	0	0	0	0	0	0	0
152	2	1	191	20	0	0	0	0	0	0	0	0	0
153	8	5	192	20	0	0	0	0	0	0	0	0	0
154	5	1	192	1	0	0	0	0	0	0	0	0	0
155	1	0	193	1	0	0	0	0	0	0	0	0	0
156	2	1	193	20	0	0	0	0	0	0	0	0	0
157	1	0	194	1	0	0	0	0	0	0	0	0	0
158	5	3	194	20	0	0	0	0	0	0	0	0	0
159	2	0	198	20	0	0	0	0	0	0	0	0	0
160	1	0	200	20	0	0	0	0	0	0	0	0	0
161	2	1	200	1	0	0	0	0	0	0	0	0	0
162	1	0	201	20	0	0	0	0	0	0	0	0	0
163	2	1	201	1	0	0	0	0	0	0	0	0	0
164	1	0	202	20	0	0	0	0	0	0	0	0	0
165	2	1	202	1	0	0	0	0	0	0	0	0	0
166	4	1	203	20	0	0	0	0	0	0	0	0	0
167	43	28	203	1	0	0	0	0	0	0	0	0	0
168	3	0	205	20	0	0	0	0	0	0	0	0	0
169	2	0	206	1	0	0	0	0	0	0	0	0	0
170	3	1	206	20	0	0	0	0	0	0	0	0	0
171	3	0	207	20	0	0	0	0	0	0	0	0	0
172	3	0	208	20	0	0	0	0	0	0	0	0	0
173	3	1	209	1	0	0	0	0	0	0	0	0	0
174	1	0	209	20	0	0	0	0	0	0	0	0	0
175	7	0	210	20	0	0	0	0	0	0	0	0	0
176	3	1	210	1	0	0	0	0	0	0	0	0	0
177	42	28	211	1	0	0	0	0	0	0	0	0	0
178	6	1	211	20	0	0	0	0	0	0	0	0	0
179	10	2	212	20	0	0	0	0	0	0	0	0	0
180	1	0	213	20	0	0	0	0	0	0	0	0	0
181	23	11	214	20	0	0	0	0	0	0	0	0	0
182	3	1	217	20	0	0	0	0	0	0	0	0	0
183	22	4	220	20	0	0	0	0	0	0	0	0	0
184	63	46	220	1	0	0	0	0	0	0	0	0	0
185	25	2	221	20	0	0	0	0	0	0	0	0	0
186	63	46	221	1	0	0	0	0	0	0	0	0	0
187	20	10	222	20	0	0	0	0	0	0	0	0	0
188	63	46	222	1	0	0	0	0	0	0	0	0	0
189	22	5	223	20	0	0	0	0	0	0	0	0	0
190	63	49	223	1	0	0	0	0	0	0	0	0	0
191	22	10	224	20	0	0	0	0	0	0	0	0	0
192	63	47	224	1	0	0	0	0	0	0	0	0	0
193	23	13	225	20	0	0	0	0	0	0	0	0	0
194	63	45	225	1	0	0	0	0	0	0	0	0	0
195	23	14	226	20	0	0	0	0	0	0	0	0	0
196	63	45	226	1	0	0	0	0	0	0	0	0	0
197	13	2	227	20	0	0	0	0	0	0	0	0	0
198	35	27	227	1	0	0	0	0	0	0	0	0	0
199	35	27	228	1	0	0	0	0	0	0	0	0	0
200	5	0	228	20	0	0	0	0	0	0	0	0	0
201	3	1	229	20	0	0	0	0	0	0	0	0	0
202	4	0	230	20	0	0	0	0	0	0	0	0	0
203	4	0	231	20	0	0	0	0	0	0	0	0	0
204	4	2	232	20	0	0	0	0	0	0	0	0	0
205	2	0	233	20	0	0	0	0	0	0	0	0	0
206	2	0	234	20	0	0	0	0	0	0	0	0	0
207	20	6	236	20	0	0	0	0	0	0	0	0	0
208	2	0	237	1	0	0	0	0	0	0	0	0	0
209	5	3	237	20	0	0	0	0	0	0	0	0	0
210	5	0	238	20	0	0	0	0	0	0	0	0	0
211	8	7	242	20	0	0	0	0	0	0	0	0	0
212	4	2	243	20	0	0	0	0	0	0	0	0	0
213	18	15	244	1	0	0	0	0	0	0	0	0	0
214	5	4	244	20	0	0	0	0	0	0	0	0	0
215	42	30	245	1	0	0	0	0	0	0	0	0	0
216	10	6	245	20	0	0	0	0	0	0	0	0	0
217	2	0	247	1	0	0	0	0	0	0	0	0	0
218	8	0	247	20	0	0	0	0	0	0	0	0	0
219	1	0	250	1	0	0	0	0	0	0	0	0	0
220	3	2	251	20	0	0	0	0	0	0	0	0	0
221	2	0	251	1	0	0	0	0	0	0	0	0	0
222	1	0	252	1	0	0	0	0	0	0	0	0	0
223	1	0	252	20	0	0	0	0	0	0	0	0	0
224	25	7	253	20	0	0	0	0	0	0	0	0	0
225	20	1	254	20	0	0	0	0	0	0	0	0	0
226	63	46	254	1	0	0	0	0	0	0	0	0	0
227	7	5	255	20	0	0	0	0	0	0	0	0	0
228	3	2	256	20	0	0	0	0	0	0	0	0	0
229	2	0	263	20	0	0	0	0	0	0	0	0	0
230	5	3	264	20	0	0	0	0	0	0	0	0	0
231	1	1	265	20	0	0	0	0	0	0	0	0	0
232	21	3	269	20	0	0	0	0	0	0	0	0	0
233	21	7	270	20	0	0	0	0	0	0	0	0	0
234	1	1	271	20	0	0	0	0	0	0	0	0	0
235	2	1	272	20	0	0	0	0	0	0	0	0	0
236	2	0	274	20	0	0	0	0	0	0	0	0	0
237	1	0	277	20	0	0	0	0	0	0	0	0	0
238	1	0	278	20	0	0	0	0	0	0	0	0	0
239	1	0	279	20	0	0	0	0	0	0	0	0	0
240	1	0	281	20	0	0	0	0	0	0	0	0	0
241	1	0	282	20	0	0	0	0	0	0	0	0	0
242	5	1	284	20	0	0	0	0	0	0	0	0	0
243	1	1	285	20	0	0	0	0	0	0	0	0	0
244	4	4	286	20	0	0	0	0	0	0	0	0	0
245	2	0	287	20	0	0	0	0	0	0	0	0	0
246	1	0	289	20	0	0	0	0	0	0	0	0	0
247	2	1	293	20	0	0	0	0	0	0	0	0	0
248	2	0	294	20	0	0	0	0	0	0	0	0	0
249	2	1	296	20	0	0	0	0	0	0	0	0	0
250	2	1	297	20	0	0	0	0	0	0	0	0	0
251	1	0	308	20	0	0	0	0	0	0	0	0	0
252	1	1	309	20	0	0	0	0	0	0	0	0	0
253	2	0	310	20	0	0	0	0	0	0	0	0	0
254	3	1	311	20	0	0	0	0	0	0	0	0	0
255	3	1	312	20	0	0	0	0	0	0	0	0	0
256	3	2	318	20	0	0	0	0	0	0	0	0	0
257	20	18	334	20	0	0	0	0	0	0	0	0	0
258	48	35	334	1	0	0	0	0	0	0	0	0	0
259	3	1	335	1	0	0	0	0	0	0	0	0	0
260	7	5	335	20	0	0	0	0	0	0	0	0	0
261	15	13	336	20	0	0	0	0	0	0	0	0	0
262	9	4	336	1	0	0	0	0	0	0	0	0	0
263	10	4	337	20	0	0	0	0	0	0	0	0	0
264	6	4	337	1	0	0	0	0	0	0	0	0	0
265	2	0	338	20	0	0	0	0	0	0	0	0	0
266	6	3	338	1	0	0	0	0	0	0	0	0	0
267	1	0	340	20	0	0	0	0	0	0	0	0	0
268	43	30	340	1	0	0	0	0	0	0	0	0	0
269	5	3	341	1	0	0	0	0	0	0	0	0	0
270	5	1	342	20	0	0	0	0	0	0	0	0	0
271	6	4	343	20	0	0	0	0	0	0	0	0	0
272	8	1	343	1	0	0	0	0	0	0	0	0	0
273	6	1	345	1	0	0	0	0	0	0	0	0	0
274	3	1	345	20	0	0	0	0	0	0	0	0	0
275	12	8	347	20	0	0	0	0	0	0	0	0	0
276	5	1	347	1	0	0	0	0	0	0	0	0	0
277	20	7	348	20	0	0	0	0	0	0	0	0	0
278	42	29	348	1	0	0	0	0	0	0	0	0	0
279	1	0	349	20	0	0	0	0	0	0	0	0	0
280	2	1	349	1	0	0	0	0	0	0	0	0	0
281	17	9	350	20	0	0	0	0	0	0	0	0	0
282	38	29	350	1	0	0	0	0	0	0	0	0	0
283	6	4	351	1	0	0	0	0	0	0	0	0	0
284	2	1	352	1	0	0	0	0	0	0	0	0	0
285	2	2	352	20	0	0	0	0	0	0	0	0	0
286	7	3	353	1	0	0	0	0	0	0	0	0	0
287	13	0	353	20	0	0	0	0	0	0	0	0	0
288	1	0	354	20	0	0	0	0	0	0	0	0	0
289	45	32	354	1	0	0	0	0	0	0	0	0	0
290	3	1	355	1	0	0	0	0	0	0	0	0	0
291	9	1	355	20	0	0	0	0	0	0	0	0	0
292	2	0	356	20	0	0	0	0	0	0	0	0	0
293	3	1	356	1	0	0	0	0	0	0	0	0	0
294	2	1	357	20	0	0	0	0	0	0	0	0	0
295	3	1	357	1	0	0	0	0	0	0	0	0	0
296	3	1	358	1	0	0	0	0	0	0	0	0	0
297	4	2	358	20	0	0	0	0	0	0	0	0	0
298	2	1	359	1	0	0	0	0	0	0	0	0	0
299	2	0	359	20	0	0	0	0	0	0	0	0	0
300	43	28	360	1	0	0	0	0	0	0	0	0	0
301	23	21	360	20	0	0	0	0	0	0	0	0	0
302	8	6	361	20	0	0	0	0	0	0	0	0	0
303	2	1	361	1	0	0	0	0	0	0	0	0	0
304	1	0	368	1	0	0	0	0	0	0	0	0	0
305	2	1	368	20	0	0	0	0	0	0	0	0	0
306	12	2	406	20	0	0	0	0	0	0	0	0	0
307	42	29	406	1	0	0	0	0	0	0	0	0	0
308	8	4	434	20	0	0	0	0	0	0	0	0	0
309	3	3	434	1	0	0	0	0	0	0	0	0	0
310	7	0	435	20	0	0	0	0	0	0	0	0	0
311	8	4	435	1	0	0	0	0	0	0	0	0	0
312	2	1	436	1	0	0	0	0	0	0	0	0	0
313	1	1	436	20	0	0	0	0	0	0	0	0	0
314	60	45	437	1	0	0	0	0	0	0	0	0	0
315	21	19	437	20	0	0	0	0	0	0	0	0	0
316	2	0	438	20	0	0	0	0	0	0	0	0	0
317	3	3	438	1	0	0	0	0	0	0	0	0	0
318	2	1	439	1	0	0	0	0	0	0	0	0	0
319	7	2	439	20	0	0	0	0	0	0	0	0	0
320	6	0	440	20	0	0	0	0	0	0	0	0	0
321	2	1	440	1	0	0	0	0	0	0	0	0	0
322	4	3	441	1	0	0	0	0	0	0	0	0	0
323	2	1	442	1	0	0	0	0	0	0	0	0	0
324	4	2	443	20	0	0	0	0	0	0	0	0	0
325	4	3	443	1	0	0	0	0	0	0	0	0	0
326	7	7	444	20	0	0	0	0	0	0	0	0	0
327	2	1	446	20	0	0	0	0	0	0	0	0	0
328	1	0	453	20	0	0	0	0	0	0	0	0	0
329	1	1	455	20	0	0	0	0	0	0	0	0	0
330	2	1	456	20	0	0	0	0	0	0	0	0	0
331	3	2	460	20	0	0	0	0	0	0	0	0	0
332	1	0	464	1	0	0	0	0	0	0	0	0	0
333	3	2	468	20	0	0	0	0	0	0	0	0	0
334	6	0	470	20	0	0	0	0	0	0	0	0	0
335	1	0	472	20	0	0	0	0	0	0	0	0	0
336	26	6	44	20	0	0	0	0	0	0	0	0	0
337	63	48	44	1	0	0	0	0	0	0	0	0	0
338	7	1	45	20	0	0	0	0	0	0	0	0	0
339	63	46	45	1	0	0	0	0	0	0	0	0	0
340	3	1	46	20	0	0	0	0	0	0	0	0	0
341	3	0	46	1	0	0	0	0	0	0	0	0	0
342	12	8	47	20	0	0	0	0	0	0	0	0	0
343	35	27	47	1	0	0	0	0	0	0	0	0	0
344	5	0	48	20	0	0	0	0	0	0	0	0	0
345	35	27	48	1	0	0	0	0	0	0	0	0	0
346	4	1	49	20	0	0	0	0	0	0	0	0	0
347	39	27	49	1	0	0	0	0	0	0	0	0	0
348	8	3	50	20	0	0	0	0	0	0	0	0	0
349	39	27	50	1	0	0	0	0	0	0	0	0	0
350	2	0	12	20	0	0	0	0	0	0	0	0	0
351	8	5	13	20	0	0	0	0	0	0	0	0	0
352	19	4	15	20	0	0	0	0	0	0	0	0	0
353	23	1	16	20	0	0	0	0	0	0	0	0	0
354	2	0	17	20	0	0	0	0	0	0	0	0	0
355	2	1	18	1	0	0	0	0	0	0	0	0	0
356	20	5	19	20	0	0	0	0	0	0	0	0	0
357	39	28	51	1	0	0	0	0	0	0	0	0	0
358	39	27	52	1	0	0	0	0	0	0	0	0	0
359	39	27	53	1	0	0	0	0	0	0	0	0	0
360	1	0	54	20	0	0	0	0	0	0	0	0	0
361	2	0	55	20	0	0	0	0	0	0	0	0	0
362	4	3	57	20	0	0	0	0	0	0	0	0	0
363	2	0	57	1	0	0	0	0	0	0	0	0	0
364	1	0	58	1	0	0	0	0	0	0	0	0	0
365	5	2	58	20	0	0	0	0	0	0	0	0	0
366	1	0	59	20	0	0	0	0	0	0	0	0	0
367	2	0	61	20	0	0	0	0	0	0	0	0	0
368	1	0	61	1	0	0	0	0	0	0	0	0	0
369	6	2	62	20	0	0	0	0	0	0	0	0	0
370	63	48	65	1	0	0	0	0	0	0	0	0	0
371	25	7	66	20	0	0	0	0	0	0	0	0	0
372	26	2	67	20	0	0	0	0	0	0	0	0	0
373	25	2	68	20	0	0	0	0	0	0	0	0	0
374	14	2	69	20	0	0	0	0	0	0	0	0	0
375	1	0	69	1	0	0	0	0	0	0	0	0	0
376	1	0	70	20	0	0	0	0	0	0	0	0	0
377	4	1	71	20	0	0	0	0	0	0	0	0	0
378	3	1	72	20	0	0	0	0	0	0	0	0	0
379	1	0	73	20	0	0	0	0	0	0	0	0	0
380	6	1	74	20	0	0	0	0	0	0	0	0	0
381	1	0	74	1	0	0	0	0	0	0	0	0	0
382	6	2	75	20	0	0	0	0	0	0	0	0	0
383	39	28	75	1	0	0	0	0	0	0	0	0	0
384	2	0	178	20	0	0	0	0	0	0	0	0	0
385	2	1	179	20	0	0	0	0	0	0	0	0	0
386	8	3	181	20	0	0	0	0	0	0	0	0	0
387	42	28	181	1	0	0	0	0	0	0	0	0	0
388	5	3	182	20	0	0	0	0	0	0	0	0	0
389	2	1	182	1	0	0	0	0	0	0	0	0	0
390	1	0	190	1	0	0	0	0	0	0	0	0	0
391	1	0	191	1	0	0	0	0	0	0	0	0	0
392	1	0	307	20	0	0	0	0	0	0	0	0	0
393	4	2	341	20	0	0	0	0	0	0	0	0	0
394	1	0	465	20	0	0	0	0	0	0	0	0	0
395	7	1	342	1	0	0	0	0	0	0	0	0	0
396	20	7	147	20	0	0	0	0	0	0	0	0	0
397	19	2	151	20	0	0	0	0	0	0	0	0	0
1000	\N	\N	401	500	1	0	0	0	0	0	0	0	0
1008	\N	\N	346	20	1	0	0	0	0	0	0	0	0
1001	\N	\N	426	20	2	0	0	0	0	0	0	0	0
1002	\N	\N	396	20	2	0	0	0	0	0	0	0	0
1003	\N	\N	218	20	2	0	0	0	0	0	0	0	0
1004	\N	\N	400	20	2	0	0	0	0	0	0	0	0
1005	\N	\N	363	20	2	0	0	0	0	0	0	0	0
1006	\N	\N	419	20	2	0	0	0	0	0	0	0	0
1007	\N	\N	322	20	2	0	0	0	0	0	0	0	0
1009	\N	\N	399	500	1	0	0	0	0	0	0	0	0
\.


--
-- Data for Name: meal_plan; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.meal_plan (meal_plan_id, created, meal_plan_type, name, user_id, target_id) FROM stdin;
500	2021-04-11 09:46:31.681524+00	Manual	meal plan 1	500	\N
501	2021-04-11 09:46:31.681524+00	Manual	meal plan 2	500	\N
503	2021-04-11 09:46:31.681524+00	Manual	meal plan 1	20	\N
504	2021-04-11 09:46:31.681524+00	Manual	meal plan 2	20	\N
506	2021-04-11 09:46:31.681524+00	Manual	meal plan 2	20	\N
505	2021-04-11 09:46:31.681524+00	Manual	meal plan 5	500	\N
\.


--
-- Data for Name: meal_plan_slot; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.meal_plan_slot (meal_plan_slot_id, dish_dish_id, meal_plan_id) FROM stdin;
500	500	500
501	501	500
502	502	500
503	500	501
504	501	501
505	502	501
506	500	503
507	501	503
508	502	503
509	500	504
510	501	504
511	502	504
512	503	505
513	504	505
\.


--
-- Data for Name: modifier_mappings; Type: TABLE DATA; Schema: public; Owner: bankuser
--

COPY public.modifier_mappings (mapping_id, modifier_type, modifier, mapped_modifier, reference_id) FROM stdin;
5189	Marker	chopped	chopped	\N
5190	Marker	chunked	chunked	\N
5191	Marker	crumbled	crumbled	\N
5192	Marker	crushed	crushed	\N
5193	Marker	cubed	cubed	\N
5194	Marker	diced	diced	\N
5195	Marker	drained	drained	\N
5196	Marker	firmly packed	firmly packed	\N
5197	Marker	frozen	frozen	\N
5198	Marker	grated	grated	\N
5199	Marker	halved	halved	\N
5200	Marker	kernels	kernels	\N
5201	Marker	loosely packed	loosely packed	\N
5202	Marker	mashed	mashed	\N
5203	Marker	melted	melted	\N
5204	Marker	packed	packed	\N
5205	Marker	pared	pared	\N
5206	Marker	pieces	pieces	\N
5207	Marker	pitted	pitted	\N
5208	Marker	powdered	powdered	\N
5209	Marker	pulp	pulp	\N
5210	Marker	pureed	pureed	\N
5211	Marker	quartered	quartered	\N
5212	Marker	rounded	rounded	\N
5213	Marker	rounds	rounds	\N
5214	Marker	sectioned	sectioned	\N
5215	Marker	shaved	shaved	\N
5216	Marker	shelled	shelled	\N
5217	Marker	shredded	shredded	\N
5218	Marker	sifted	sifted	\N
5219	Marker	sliced	sliced	\N
5220	Marker	slivered	slivered	\N
5221	Marker	solid	solid	\N
5222	Marker	undrained	undrained	\N
5223	Marker	unpacked	unpacked	\N
5224	Marker	unsifted	unsifted	\N
5225	Marker	unthawed	unthawed	\N
5226	Marker	wedge	wedge	\N
5227	Marker	whole	whole	\N
5228	Marker	chopped	chunked	\N
5229	Marker	chopped	cubed	\N
5230	Marker	chopped	diced	\N
5231	Marker	chunked	chopped	\N
5232	Marker	chunked	cubed	\N
5233	Marker	chunked	diced	\N
5234	Marker	cubed	chopped	\N
5235	Marker	cubed	chunked	\N
5236	Marker	cubed	diced	\N
5237	Marker	diced	chopped	\N
5238	Marker	diced	chunked	\N
5239	Marker	diced	cubed	\N
5240	Marker	sliced	slivered	\N
5241	Marker	slivered	sliced	\N
5242	Marker	grated	shredded	\N
5243	Marker	shredded	grated	\N
5244	Marker	firmly packed	loosely packed	\N
5245	Marker	firmly packed	packed	\N
5246	Marker	loosely packed	firmly packed	\N
5247	Marker	loosely packed	packed	\N
5248	Marker	packed	firmly packed	\N
5249	Marker	packed	loosely packed	\N
5250	Marker	mashed	pureed	\N
5251	Marker	pureed	mashed	\N
5252	UnitSize	extra large	extra large	\N
5253	UnitSize	large	large	\N
5254	UnitSize	medium	medium	\N
5255	UnitSize	small	small	\N
5256	UnitSize	extra-large	extra large	\N
5257	UnitSize	xl	extra large	\N
5258	UnitSize	lg	large	\N
5259	UnitSize	lg.	large	\N
5260	UnitSize	med.	medium	\N
5261	UnitSize	med	medium	\N
5262	UnitSize	sm	small	\N
5263	UnitSize	sm.	small	\N
\.


--
-- Data for Name: proposal; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.proposal (proposal_id, user_id, is_refreshable, created) FROM stdin;
500	20	f	2021-04-11 09:46:31.681524+00
501	20	f	2021-04-11 09:46:31.681524+00
502	20	f	2021-04-11 09:46:31.681524+00
\.


--
-- Data for Name: proposal_context; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.proposal_context (proposal_context_id, proposal_id, current_attempt_index, current_approach_type, current_approach_index, meal_plan_id, target_id, target_hash_code, proposal_hash_code) FROM stdin;
500	500	\N	WHEEL_MIXED	0	\N	500	-623001283	0
502	502	\N	WHEEL_MIXED	0	\N	501	\N	\N
501	501	\N	WHEEL_MIXED	0	\N	501	-623001283	0
\.


--
-- Data for Name: proposal_approach; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.proposal_approach (proposal_approach_id, proposal_context_id, approach_number, instructions) FROM stdin;
500	500	0	4;1;2;3
510	501	0	4;1;3
511	501	1	1;3;4
510	501	0	4;1;3
511	501	1	1;3;4
\.


--
-- Data for Name: proposal_slot; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.proposal_slot (slot_id, slot_number, flat_matched_tag_ids, proposal_id, picked_dish_id, slot_dish_tag_id) FROM stdin;
500	1	\N	500	\N	320
501	2	\N	500	66	320
502	3	\N	500	\N	320
503	4	\N	500	\N	320
510	1	\N	500	\N	320
511	2	\N	501	66	320
512	3	\N	501	\N	320
513	4	\N	501	\N	320
620	1	\N	502	96	320
521	2	\N	502	66	320
522	3	\N	502	105	320
523	4	\N	502	115	320
\.


--
-- Data for Name: proposal_dish; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.proposal_dish (dish_slot_id, slot_id, dish_id, matched_tag_ids) FROM stdin;
500	500	96	89
501	500	37	89
502	500	5	64
503	500	42	64
504	500	39	322
505	501	106	322;64;321
507	501	16	322;399;321
508	501	103	322;64;321
509	501	76	322;321
510	502	65	322;399;406
511	502	105	322;64;406
512	502	104	322;406
513	502	64	406
514	502	52	406
515	503	115	322;81
516	503	61	81
517	503	63	81
518	503	10	81
519	503	77	81
530	510	96	89
531	510	37	89
532	510	5	64
533	510	42	64
534	510	39	322
535	511	106	322;64;321
537	511	16	322;399;321
538	511	103	322;64;321
539	511	76	322;321
540	512	65	322;399;406
541	512	105	322;64;406
542	512	104	322;406
543	512	64	406
544	512	52	406
545	513	115	322;81
546	513	61	81
547	513	63	81
548	513	10	81
549	513	77	81
631	510	37	89
632	510	5	64
633	510	42	64
634	510	39	322
635	511	106	322;64;321
637	521	16	322;399;321
638	521	103	322;64;321
639	521	76	322;321
641	522	65	322;399;406
642	522	104	322;406
643	522	64	406
644	522	52	406
646	523	61	81
647	523	63	81
648	523	10	81
649	523	77	81
\.


--
-- Data for Name: shadow_tags; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.shadow_tags (shadow_tag_id, dish_id, tag_id) FROM stdin;
\.


--
-- Data for Name: tag_relation; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.tag_relation (tag_relation_id, child_tag_id, parent_tag_id) FROM stdin;
238	126	393
239	163	393
240	20	393
241	67	393
242	70	393
243	230	394
244	340	394
245	74	394
246	89	394
247	395	292
248	396	292
249	397	292
250	398	292
251	399	291
252	400	291
253	401	291
254	402	291
255	403	\N
256	7	407
257	263	410
258	78	423
259	114	423
260	234	423
261	244	423
262	260	423
263	199	429
264	204	429
265	434	368
266	435	372
267	436	376
268	437	368
269	438	376
270	439	376
271	440	375
272	441	375
273	442	375
274	443	368
275	444	378
276	445	369
277	446	423
278	447	14
279	448	370
280	449	379
281	450	379
282	451	390
283	452	390
284	453	381
285	454	386
286	455	423
287	456	423
288	457	379
289	458	392
290	459	431
291	460	382
292	461	392
293	462	378
294	463	388
295	464	377
296	465	388
297	466	376
298	467	388
299	468	403
300	469	384
301	470	367
302	471	\N
303	472	421
304	473	375
1	30	9
2	35	\N
3	11	35
4	39	\N
5	40	\N
6	49	14
7	87	\N
8	31	88
9	123	14
10	157	\N
11	8	157
12	93	216
13	148	216
14	246	\N
15	197	246
16	236	313
17	265	313
18	275	313
19	283	313
20	94	367
21	235	367
22	115	368
23	369	\N
24	86	369
25	370	\N
26	72	370
27	85	370
28	136	370
29	137	370
30	158	370
31	196	370
32	198	370
33	280	370
34	281	370
35	4	370
36	88	371
37	9	371
38	90	372
39	124	372
40	251	372
41	257	372
42	258	372
43	38	373
44	46	373
45	159	374
46	28	375
47	73	375
48	162	375
49	233	375
50	311	375
51	61	376
52	91	376
53	105	376
54	117	376
55	121	376
56	134	376
57	161	376
58	201	376
59	47	377
60	50	377
61	54	377
62	57	377
63	112	377
64	168	377
65	169	377
66	171	377
67	173	377
68	184	377
69	185	377
70	192	377
71	193	377
72	194	377
73	232	377
74	242	377
75	243	377
76	255	377
77	259	377
78	271	377
79	272	377
80	286	377
81	308	377
82	309	377
83	69	378
84	106	378
85	146	378
86	170	378
87	203	378
88	29	379
89	129	379
90	133	379
91	183	379
92	250	379
93	256	379
94	261	379
95	195	380
96	3	380
97	5	380
98	36	381
99	37	381
100	51	381
101	71	381
102	122	381
103	125	381
104	131	381
105	202	381
106	248	381
107	277	381
108	279	381
109	128	382
110	175	382
111	176	382
112	177	382
113	178	382
114	179	382
115	181	382
116	182	382
117	274	382
118	293	382
119	294	382
120	295	382
121	350	382
122	361	382
123	80	382
124	84	382
125	383	\N
126	63	383
127	144	383
128	145	383
129	180	383
130	270	383
131	384	\N
132	149	384
133	150	384
134	276	384
135	385	379
136	77	385
137	318	385
138	336	385
139	347	385
140	386	\N
141	22	386
142	25	386
143	99	386
144	102	386
145	110	386
146	113	386
147	127	386
148	130	386
149	164	386
150	188	386
151	189	386
152	190	386
153	209	386
154	210	386
155	217	386
156	231	386
157	312	386
158	338	386
159	339	386
160	341	386
161	353	386
162	356	386
163	100	388
164	101	388
165	107	388
166	108	388
167	111	388
168	116	388
169	118	388
170	119	388
171	132	388
172	151	388
173	167	388
174	187	388
175	200	388
176	208	388
177	211	388
178	213	388
179	262	388
180	287	388
181	288	388
182	349	388
183	354	388
184	357	388
185	33	388
186	34	388
187	43	388
188	48	388
189	52	388
190	58	388
191	60	388
192	141	389
193	214	389
194	225	389
195	226	389
196	390	388
197	103	390
198	104	390
199	109	390
200	120	390
201	135	390
202	191	390
203	207	390
204	212	390
205	247	390
206	249	390
207	284	390
208	289	390
209	310	390
210	355	390
211	15	390
212	16	390
213	32	390
214	45	390
215	53	390
216	55	390
217	56	390
218	59	390
219	65	390
220	75	390
221	76	390
222	81	390
223	97	390
224	98	390
225	391	\N
226	321	391
227	322	391
228	324	391
229	325	391
230	326	391
231	392	\N
232	221	392
233	252	392
234	273	392
235	278	392
236	23	392
237	393	378
305	331	369
306	245	403
307	307	403
308	267	404
309	268	404
310	165	405
311	186	405
312	264	405
313	406	368
314	24	407
315	142	407
316	44	409
317	62	409
318	138	409
319	147	409
320	206	409
321	238	409
322	282	409
323	41	410
324	172	410
325	285	410
326	42	411
327	139	411
328	237	411
329	92	412
330	166	412
331	95	413
332	96	413
333	140	413
334	205	413
335	253	414
336	27	421
337	269	421
338	14	423
339	26	423
340	389	\N
341	10	\N
342	83	\N
343	160	\N
344	216	\N
345	218	160
346	219	160
347	290	\N
348	291	\N
349	292	\N
350	298	216
351	302	246
352	303	246
353	305	\N
354	300	305
355	306	\N
356	313	\N
357	215	313
358	222	313
359	254	313
360	299	313
361	316	160
362	317	160
363	319	292
364	320	\N
365	323	157
366	327	306
367	328	306
368	329	291
369	330	290
370	332	\N
371	2	332
372	64	332
373	315	332
374	333	\N
375	155	369
376	344	332
377	363	306
378	364	306
379	365	306
380	367	\N
381	223	367
382	224	367
383	377	\N
384	174	377
385	296	377
386	297	377
387	334	377
388	335	377
389	343	377
390	351	377
391	358	377
392	360	377
393	378	\N
394	68	378
395	337	378
396	348	378
397	379	\N
398	352	379
399	359	379
400	380	\N
401	314	380
402	381	378
403	18	381
404	345	381
405	382	\N
406	79	382
407	371	\N
408	372	371
409	373	\N
410	374	371
411	6	374
412	368	374
413	375	371
414	228	375
415	376	\N
416	387	\N
417	388	\N
418	19	388
419	21	388
420	227	388
421	342	388
422	304	369
423	404	87
424	405	\N
425	1	405
426	17	405
427	407	\N
428	301	407
429	408	370
430	409	\N
431	229	410
432	411	\N
433	412	\N
434	413	\N
435	12	413
436	220	413
437	414	\N
438	415	332
439	416	160
440	417	305
441	418	370
442	419	305
443	420	370
444	421	371
445	394	421
446	422	305
447	423	\N
448	13	423
449	82	423
450	410	423
451	424	305
452	425	290
453	426	290
454	427	290
455	428	290
456	429	\N
457	346	429
458	430	423
459	66	430
460	431	373
461	432	\N
462	433	\N
463	143	197
464	152	197
465	153	197
466	154	197
467	156	197
468	239	\N
469	240	\N
470	241	\N
471	266	\N
9000	505	393
9001	506	393
1000	1001	1000
1001	1002	1000
1002	1003	1000
1003	1004	1001
1004	1005	1001
1005	1006	369
1006	1007	1006
1007	1008	1007
1008	1010	1009
1009	1011	1009
1010	1012	1009
1011	1013	1010
1012	1014	1010
1013	1015	369
1014	1016	1015
1015	1017	1016
1016	1019	1018
1017	1020	1018
1018	1021	1018
1019	1022	1019
1020	1023	1019
1021	1024	369
1022	1025	1024
1023	1026	1025
1024	1028	1027
1025	1029	1027
1026	1030	1027
1027	1031	1028
1028	1032	1028
1029	1033	369
1030	1034	1033
1031	1035	1034
1032	1037	1036
1033	1038	1036
1034	1039	1036
1035	1040	1037
1036	1041	1037
1037	1042	369
1038	1043	1042
1039	1044	1043
1040	1046	1045
1041	1047	1045
1042	1048	1045
1043	1049	1046
1044	1050	1046
1045	1051	369
1046	1052	1051
1047	1053	1052
1048	1055	1054
1049	1056	1054
1050	1057	1054
1051	1058	1055
1052	1059	1055
1053	1060	369
1054	1061	1060
1055	1062	1061
1056	1064	1063
1057	1065	1063
1058	1066	1063
1059	1067	1064
1060	1068	1064
1061	1069	369
1062	1070	1069
1063	1071	1070
1064	1073	1072
1065	1074	1072
1066	1075	1072
1067	1076	1073
1068	1077	1073
1069	1078	369
1070	1079	1078
1071	1080	1079
1072	1082	1081
1073	1083	1081
1074	1084	1081
1075	1085	1082
1076	1086	1082
1077	1087	369
1078	1088	1087
1079	1089	1088
1080	1091	1090
1081	1092	1090
1082	1093	1090
1083	1094	1091
1084	1095	1091
1085	1096	369
1086	1097	1096
1087	1098	1097
1088	1100	1099
1089	1101	1099
1090	1102	1099
1091	1103	1100
1092	1104	1100
1093	1105	369
1094	1106	1105
1095	1107	1106
1096	1109	1108
1097	1110	1108
1098	1111	1108
1099	1112	1109
1100	1113	1109
1101	1114	369
1102	1115	1114
1103	1116	1115
1104	1118	1117
1105	1119	1117
1106	1120	1117
1107	1121	1118
1108	1122	1118
1109	1123	369
1110	1124	1123
1111	1125	1124
1112	1128	1127
1113	1129	1127
1114	1130	1127
1115	1131	1128
1116	1132	1128
1117	1133	369
1118	1134	1133
1119	1135	1134
1120	1137	1136
1121	1138	1136
1122	1139	1136
1123	1140	1137
1124	1141	1137
1125	1142	369
1126	1143	1142
1127	1144	1143
1128	1146	1145
1129	1147	1145
1130	1148	1145
1131	1149	1146
1132	1150	1146
1133	1151	369
1134	1152	1151
1135	1153	1152
1136	1154	43
1137	1156	1155
1138	1157	1155
1139	1158	1155
1140	1159	1156
1141	1160	1156
1142	1161	369
1143	1162	1161
1144	1163	1162
1145	1165	1164
1146	1166	1164
1147	1167	1164
1148	1168	1165
1149	1169	1165
1150	1170	369
1151	1171	1170
1152	1172	1171
1153	1174	1173
1154	1175	1173
1155	1176	1173
1156	1177	1174
1157	1178	1174
1158	1179	369
1159	1180	1179
1160	1181	1180
1161	1183	1182
1162	1184	1182
1163	1185	1182
1164	1186	1183
1165	1187	1183
1166	1188	369
1167	1189	1188
1168	1190	1189
\.


--
-- Data for Name: target; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.target (target_id, created, last_updated, last_used, target_name, target_tag_ids, user_id, proposal_id, target, expires, target_type) FROM stdin;
500	2018-05-21 09:15:22.451+00	\N	\N	testing	64;322;399	20	\N	TargetEntity	\N	Standard
501	2018-05-21 09:15:22.451+00	\N	\N	testing	64;322;399	20	\N	TargetEntity	\N	Standard
502	2018-05-21 09:15:22.451+00	\N	\N	testing	64;322;399	500	\N	TargetEntity	\N	Standard
503	2018-05-21 09:15:22.451+00	\N	\N	testing	64;322;399	500	\N	TargetEntity	2018-05-21 10:15:22.451+00	Standard
\.


--
-- Data for Name: target_slot; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.target_slot (target_slot_id, slot_dish_tag_id, slot_order, target_id, target_tag_ids, target) FROM stdin;
500	320	1	500	406	TargetSlotEntity
501	320	2	500	321	TargetSlotEntity
502	320	3	500	81	TargetSlotEntity
503	320	4	500	89	TargetSlotEntity
504	320	1	501	406	TargetSlotEntity
505	320	2	501	321	TargetSlotEntity
506	320	3	501	81	TargetSlotEntity
507	320	4	501	89	TargetSlotEntity
508	320	1	502	406;301	TargetSlotEntity
509	320	1	502	329;301	TargetSlotEntity
510	320	1	502	81;301	TargetSlotEntity
511	320	1	502	89;301	TargetSlotEntity
512	320	1	503	406;301	TargetSlotEntity
513	320	1	503	329;301	TargetSlotEntity
514	320	1	503	81;301	TargetSlotEntity
515	320	1	503	89;301	TargetSlotEntity
\.


--
-- Data for Name: tokens; Type: TABLE DATA; Schema: public; Owner: bankuser
--

COPY public.tokens (token_id, created_on, token_type, token_value, user_id) FROM stdin;
\.


--
-- Data for Name: user_devices; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.user_devices (user_device_id, user_id, name, model, os, os_version, client_type, build_number, client_device_id, client_version, token, last_login) FROM stdin;
\.


--
-- Data for Name: user_properties; Type: TABLE DATA; Schema: public; Owner: bankuser
--

COPY public.user_properties (user_property_id, user_id, property_key, property_value, is_system) FROM stdin;
50	500	test_property	ho hum value	\N
51	500	another_property	good value	\N
\.


--
-- Name: authority_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.authority_id_seq', 10000, false);


--
-- Name: authority_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.authority_seq', 1, false);


--
-- Name: auto_tag_instructions_sequence; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.auto_tag_instructions_sequence', 10000, false);


--
-- Name: campaign_sequence; Type: SEQUENCE SET; Schema: public; Owner: bankuser
--

SELECT pg_catalog.setval('public.campaign_sequence', 1000, false);


--
-- Name: category_relation_sequence; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.category_relation_sequence', 10000, false);


--
-- Name: dish_item_sequence; Type: SEQUENCE SET; Schema: public; Owner: bankuser
--

SELECT pg_catalog.setval('public.dish_item_sequence', 2536, true);


--
-- Name: dish_sequence; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.dish_sequence', 10000, false);


--
-- Name: domain_unit_sequence; Type: SEQUENCE SET; Schema: public; Owner: bankuser
--

SELECT pg_catalog.setval('public.domain_unit_sequence', 1098, true);


--
-- Name: factor_sequence; Type: SEQUENCE SET; Schema: public; Owner: bankuser
--

SELECT pg_catalog.setval('public.factor_sequence', 999, true);


--
-- Name: food_category_mapping_seq; Type: SEQUENCE SET; Schema: public; Owner: bankuser
--

SELECT pg_catalog.setval('public.food_category_mapping_seq', 1000, false);


--
-- Name: hibernate_sequence; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.hibernate_sequence', 1, false);


--
-- Name: list_item_sequence; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.list_item_sequence', 10000, false);


--
-- Name: list_layout_category_sequence; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.list_layout_category_sequence', 10000, false);


--
-- Name: list_layout_sequence; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.list_layout_sequence', 10000, false);


--
-- Name: list_sequence; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.list_sequence', 10000, false);


--
-- Name: list_tag_stats_sequence; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.list_tag_stats_sequence', 10000, false);


--
-- Name: meal_plan_sequence; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.meal_plan_sequence', 10000, false);


--
-- Name: meal_plan_slot_sequence; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.meal_plan_slot_sequence', 10000, false);


--
-- Name: modifier_mapping_sequence; Type: SEQUENCE SET; Schema: public; Owner: bankuser
--

SELECT pg_catalog.setval('public.modifier_mapping_sequence', 1000, false);


--
-- Name: proposal_approach_sequence; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.proposal_approach_sequence', 10000, false);


--
-- Name: proposal_context_sequence; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.proposal_context_sequence', 10000, false);


--
-- Name: proposal_context_slot_sequence; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.proposal_context_slot_sequence', 10000, false);


--
-- Name: proposal_dish_sequence; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.proposal_dish_sequence', 10000, false);


--
-- Name: proposal_sequence; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.proposal_sequence', 10000, false);


--
-- Name: proposal_slot_sequence; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.proposal_slot_sequence', 10000, false);


--
-- Name: shadow_tags_sequence; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.shadow_tags_sequence', 10000, false);


--
-- Name: tag_relation_sequence; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.tag_relation_sequence', 10000, false);


--
-- Name: tag_search_group_sequence; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.tag_search_group_sequence', 1, false);


--
-- Name: tag_sequence; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.tag_sequence', 10000, false);


--
-- Name: target_proposal_dish_sequence; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.target_proposal_dish_sequence', 10000, false);


--
-- Name: target_proposal_sequence; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.target_proposal_sequence', 10000, false);


--
-- Name: target_proposal_slot_sequence; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.target_proposal_slot_sequence', 10000, false);


--
-- Name: target_sequence; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.target_sequence', 10000, false);


--
-- Name: target_slot_sequence; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.target_slot_sequence', 10000, false);


--
-- Name: token_sequence; Type: SEQUENCE SET; Schema: public; Owner: bankuser
--

SELECT pg_catalog.setval('public.token_sequence', 57000, false);


--
-- Name: unit_sequence; Type: SEQUENCE SET; Schema: public; Owner: bankuser
--

SELECT pg_catalog.setval('public.unit_sequence', 1000, false);


--
-- Name: user_device_sequence; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.user_device_sequence', 1, false);


--
-- Name: user_id_sequence; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.user_id_sequence', 10000, false);


--
-- Name: user_properties_id_seq; Type: SEQUENCE SET; Schema: public; Owner: bankuser
--

SELECT pg_catalog.setval('public.user_properties_id_seq', 10000, false);


--
-- PostgreSQL database dump complete
--

