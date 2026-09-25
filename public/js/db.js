/**
 * Sitaram Ayurveda Medicine Catalogue Admin Website
 * Central Database & Persistence Layer
 * Designed for immediate Admin Web use and future Android User/Admin application sync.
 */

const DB_KEY_PRODUCTS = 'sitaram_products_v1';
const DB_KEY_CATEGORIES = 'sitaram_categories_v1';
const DB_KEY_INGREDIENTS = 'sitaram_ingredients_v1';
const DB_KEY_MANUFACTURERS = 'sitaram_manufacturers_v1';
const DB_KEY_BANNERS = 'sitaram_banners_v1';
const DB_KEY_ANNOUNCEMENTS = 'sitaram_announcements_v1';
const DB_KEY_AUDIT = 'sitaram_audit_logs_v1';
const DB_KEY_SETTINGS = 'sitaram_settings_v1';

// The 24 Categories from Sitaram Ayurveda Therapeutic Index Handbook
const DEFAULT_CATEGORIES = [
  { id: 'cat_arishtam', code: 'ARI', name: 'Arishtam', description: 'Self-generated alcohol based decoction elixirs', order: 1, status: 'Active', productCount: 0 },
  { id: 'cat_asavam', code: 'ASA', name: 'Asavam', description: 'Fermented medicinal infusions with natural digestive kindling properties', order: 2, status: 'Active', productCount: 0 },
  { id: 'cat_arkam', code: 'ARK', name: 'Arkam', description: 'Distilled herbal essences and aromatic liquid extracts', order: 3, status: 'Active', productCount: 0 },
  { id: 'cat_bhasmas', code: 'BHA', name: 'Bhasmas / Ksharams', description: 'Calcined mineral, metallic and alkaline preparations of high bioavailability', order: 4, status: 'Active', productCount: 0 },
  { id: 'cat_choornams', code: 'CHO', name: 'Choornams', description: 'Finely pulverized classical herbal powders and compound churna mixtures', order: 5, status: 'Active', productCount: 0 },
  { id: 'cat_gulika', code: 'GUL', name: 'Gulika, Gulika Tablets, Capsules', description: 'Classical hand-rolled pills, modern compressed tablets and hard gelatin formulations', order: 6, status: 'Active', productCount: 0 },
  { id: 'cat_single_herb', code: 'SHC', name: 'Single Herb Veg Capsules', description: 'Standardized pure single herb extracts encapsulated in 100% vegetarian shells', order: 7, status: 'Active', productCount: 0 },
  { id: 'cat_kashayams', code: 'KAS', name: 'Kashayams', description: 'Concentrated classical aqueous decoctions (Kwatha) for systemic disorders', order: 8, status: 'Active', productCount: 0 },
  { id: 'cat_kashayam_tabs', code: 'KTB', name: 'Kashayam Tablets', description: 'Solidified Kashayam extracts compressed for modern compliance without bitter taste', order: 9, status: 'Active', productCount: 0 },
  { id: 'cat_kashayam_sachet', code: 'KSC', name: 'Preservative Free Kashayam Sachet', description: 'Sterile ready-to-mix sachets without chemical preservatives', order: 10, status: 'Active', productCount: 0 },
  { id: 'cat_lehyams', code: 'LEH', name: 'Lehyams', description: 'Herbal jams, confections and electuaries with honey and raw jaggery', order: 11, status: 'Active', productCount: 0 },
  { id: 'cat_ghruthams', code: 'GHR', name: 'Ghruthams', description: 'Medicated cows ghee formulations targeting nervous, ocular and reproductive tissue', order: 12, status: 'Active', productCount: 0 },
  { id: 'cat_avartis', code: 'AVA', name: 'Avartis', description: 'Potentiated preparations processed repeatedly (e.g. 21 times, 101 times)', order: 13, status: 'Active', productCount: 0 },
  { id: 'cat_soft_gels', code: 'SGC', name: 'Soft Gel Capsules', description: 'Lipid soluble herbal extracts and medicated oils in soft gelatin encapsulation', order: 14, status: 'Active', productCount: 0 },
  { id: 'cat_seviyams', code: 'SEV', name: 'Seviyams / Vasthi Thailams', description: 'Medicated oils specifically formulated for internal ingestion and therapeutic enemas', order: 15, status: 'Active', productCount: 0 },
  { id: 'cat_eranda_thailams', code: 'ERT', name: 'Eranda Thailams', description: 'Castor oil based formulations processed with specific herbs for purgation & arthritis', order: 16, status: 'Active', productCount: 0 },
  { id: 'cat_thailams', code: 'THA', name: 'Thailams', description: 'Classical sesame oil based medicated oils for external abhyanga and local therapy', order: 17, status: 'Active', productCount: 0 },
  { id: 'cat_kera_thailams', code: 'KTH', name: 'Kera Thailams', description: 'Pure coconut oil based preparations traditional to Kerala for hair, scalp and skin', order: 18, status: 'Active', productCount: 0 },
  { id: 'cat_kuzhambus', code: 'KUZ', name: 'Kuzhambus', description: 'Dense triple-base medicated oils (Sesame, Castor, Ghee) for deep tissue musculoskeletal healing', order: 19, status: 'Active', productCount: 0 },
  { id: 'cat_mukkootu', code: 'MUK', name: 'Mukkootu', description: 'Traditional Kerala combination oils processed with three sacred oil bases', order: 20, status: 'Active', productCount: 0 },
  { id: 'cat_lepams', code: 'LEP', name: 'Lepams / Ointments', description: 'External medicinal pastes, liniments, creams and topical balms', order: 21, status: 'Active', productCount: 0 },
  { id: 'cat_rasakriya', code: 'RAS', name: 'Rasakriya', description: 'Concentrated aqueous extracts evaporated into solid therapeutic pastilles', order: 22, status: 'Active', productCount: 0 },
  { id: 'cat_otc', code: 'OTC', name: 'O.T.C Products', description: 'Over-the-counter daily wellness, immunity balms, herbal cough drops and digestive aids', order: 23, status: 'Active', productCount: 0 },
  { id: 'cat_ethical_patents', code: 'ETH', name: 'Ethical Patents', description: 'Proprietary research-backed formulations developed by Sitaram Ayurveda', order: 24, status: 'Active', productCount: 0 }
];

const DEFAULT_MANUFACTURERS = [
  {
    id: 'mfg_sitaram_main',
    name: 'Sitaram Ayurveda Pvt. Ltd.',
    code: 'SAPL-01',
    license: 'AYUSH-KL-TCR-1921-GMP',
    contactPerson: 'Dr. D. Ramanathan, Chief Physician',
    email: 'regulatory@sitaramayurveda.com',
    phone: '+91 487 2381201',
    address: 'Round South, Thrissur, Kerala 680001, India',
    status: 'Active'
  },
  {
    id: 'mfg_sitaram_pharmacy',
    name: 'Sitaram Pharmacy Works',
    code: 'SPW-02',
    license: 'AYUSH-KL-TCR-1948-MFG',
    contactPerson: 'Quality Assurance Head',
    email: 'qa@sitaramayurveda.com',
    phone: '+91 487 2381502',
    address: 'Punkunnam, Thrissur, Kerala 680002, India',
    status: 'Active'
  }
];

// Realistic Sitaram Ayurveda Formulations directly from the Therapeutic Index
const DEFAULT_PRODUCTS = [
  {
    id: 'prod_abhayarishtam',
    code: 'SA-00001',
    name: 'Abhayarishtam',
    category: 'Arishtam',
    classicalReference: 'Ashtangahrudayam',
    packings: ['450 ml', '200 ml'],
    ingredients: ['Abhaya (Terminalia chebula)', 'Dhatri (Emblica officinalis)', 'Kapitha (Feronia elephantum)', 'Vishala (Citrullus colocynthis)', 'Dhataki (Woodfordia fruticosa)', 'Guda (Jaggery)'],
    usage: '15 to 25 ml twice daily after food or as directed by the Ayurvedic physician.',
    indications: 'Arshas (Hemorrhoids / Piles), Udara (Ascites & Abdominal disorders), Mutra vibanda (Urinary retention), Vibanda (Constipation), Agnimandya (Loss of appetite / Weak digestion).',
    description: 'Abhayarishtam is a premier classical fermented elixir renowned for harmonizing Apana Vata, relieving stubborn piles and promoting gentle bowel peristalsis without griping.',
    imageUrl: 'https://images.unsplash.com/photo-1546868871-7041f2a55e12?w=600&auto=format&fit=crop&q=80',
    status: 'Active',
    manufacturer: 'Sitaram Ayurveda Pvt. Ltd.',
    featured: true,
    createdAt: '2026-08-01T10:00:00Z',
    updatedAt: '2026-09-12T14:30:00Z'
  },
  {
    id: 'prod_amrutharishtam',
    code: 'SA-00002',
    name: 'Amrutharishtam',
    category: 'Arishtam',
    classicalReference: 'Bhaishajya Ratnavali',
    packings: ['450 ml'],
    ingredients: ['Amrutha / Guduchi (Tinospora cordifolia)', 'Dashamoola', 'Parpata (Fumaria parviflora)', 'Katuki (Picrorhiza kurroa)', 'Musta (Cyperus rotundus)', 'Dhataki'],
    usage: '15 to 25 ml twice daily after meals with equal quantity of boiled warm water.',
    indications: 'Jwara (Chronic, intermittent and relapsing fevers), Jeerna Jwara (Post-viral debility), Pandu (Anemia), Aruchi (Anorexia), Yakrit-Pleeha vikara (Hepato-splenomegaly).',
    description: 'Classical Guduchi-centered fermented preparation indicated in chronic febrile states, autoimmune inflammation and systemic toxic Ama accumulation.',
    imageUrl: 'https://images.unsplash.com/photo-1615485290382-441e4d049cb5?w=600&auto=format&fit=crop&q=80',
    status: 'Active',
    manufacturer: 'Sitaram Ayurveda Pvt. Ltd.',
    featured: false,
    createdAt: '2026-08-02T11:00:00Z',
    updatedAt: '2026-09-10T16:15:00Z'
  },
  {
    id: 'prod_draksharishtam',
    code: 'SA-00003',
    name: 'Draksharishtam',
    category: 'Arishtam',
    classicalReference: 'Sharangadhara Samhita',
    packings: ['450 ml', '200 ml'],
    ingredients: ['Draksha (Vitis vinifera)', 'Dhataki (Woodfordia fruticosa)', 'Twak (Cinnamomum zeylanicum)', 'Ela (Elettaria cardamomum)', 'Patra (Cinnamomum tamala)', 'Priyangu (Callicarpa macrophylla)'],
    usage: '15 to 25 ml twice daily after meals.',
    indications: 'Urakshata (Chest trauma & pulmonary debility), Kasa (Chronic cough), Shwasa (Asthma & Breathlessness), Kshaya (Emaciation), Daurbalya (General weakness), Malabandha.',
    description: 'Sweet, invigorating classical nutritive tonic formulated with black raisins. Restores vital respiratory strength and nourishes Rakta and Mamsa Dhatus.',
    imageUrl: 'https://images.unsplash.com/photo-1546868871-7041f2a55e12?w=600&auto=format&fit=crop&q=80',
    status: 'Active',
    manufacturer: 'Sitaram Ayurveda Pvt. Ltd.',
    featured: true,
    createdAt: '2026-08-03T09:15:00Z',
    updatedAt: '2026-09-11T09:00:00Z'
  },
  {
    id: 'prod_ashwagandharishtam',
    code: 'SA-00004',
    name: 'Ashwagandharishtam',
    category: 'Arishtam',
    classicalReference: 'Bhaishajya Ratnavali',
    packings: ['450 ml'],
    ingredients: ['Ashwagandha (Withania somnifera)', 'Mushali (Asparagus adscendens)', 'Manjistha (Rubia cordifolia)', 'Haridra (Curcuma longa)', 'Yashtimadhu (Glycyrrhiza glabra)', 'Rasna (Pluchea lanceolata)'],
    usage: '15 to 25 ml twice daily after food.',
    indications: 'Murcha (Loss of consciousness / Syncope), Apasmara (Epilepsy), Shosha (Wasting disease), Karshya (Underweight), Arsha, Vata roga, Insomnia, Chronic stress.',
    description: 'Supreme neurological and adaptogenic nervine tonic. Stabilizes Prana and Vata, replenishes depleted Ojas and strengthens neuromuscular coordination.',
    imageUrl: 'https://images.unsplash.com/photo-1615485290382-441e4d049cb5?w=600&auto=format&fit=crop&q=80',
    status: 'Active',
    manufacturer: 'Sitaram Ayurveda Pvt. Ltd.',
    featured: true,
    createdAt: '2026-08-04T14:20:00Z',
    updatedAt: '2026-09-08T11:45:00Z'
  },
  {
    id: 'prod_kanakasavam',
    code: 'SA-00005',
    name: 'Kanakasavam',
    category: 'Asavam',
    classicalReference: 'Bhaishajya Ratnavali',
    packings: ['450 ml'],
    ingredients: ['Kanaka (Datura metel)', 'Vasa (Adhatoda vasica)', 'Yashtimadhu', 'Pippali (Piper longum)', 'Kantakari (Solanum surattense)', 'Nagakeshara (Mesua ferrea)', 'Dhataki'],
    usage: '10 to 20 ml with equal quantity of boiled cooled water twice daily after food.',
    indications: 'Kasa (Bronchial cough), Shwasa (Bronchial asthma & spasmodic dyspnea), Rajayakshma (Phthisis / Tuberculosis), Urakshata (Chest lesion), Kshaya.',
    description: 'Potent bronchodilator and antispasmodic asavam that eases tight wheezing and clears mucous plugs from the pulmonary channels.',
    imageUrl: 'https://images.unsplash.com/photo-1546868871-7041f2a55e12?w=600&auto=format&fit=crop&q=80',
    status: 'Active',
    manufacturer: 'Sitaram Ayurveda Pvt. Ltd.',
    featured: false,
    createdAt: '2026-08-05T12:00:00Z',
    updatedAt: '2026-09-05T15:10:00Z'
  },
  {
    id: 'prod_triphala_choornam',
    code: 'SA-00006',
    name: 'Triphala Choornam',
    category: 'Choornams',
    classicalReference: 'Chakradatta & Ashtangahrudayam',
    packings: ['100 g', '200 g', '500 g'],
    ingredients: ['Haritaki (Terminalia chebula)', 'Bibhitaki (Terminalia bellirica)', 'Amalaki (Emblica officinalis)'],
    usage: '3 to 5 grams at bedtime with warm water or lukewarm milk, or mixed with honey.',
    indications: 'Vibanda (Chronic constipation), Netra roga (Ophthalmic disorders), Prameha (Urinary & metabolic dysfunction, Diabetes), Deepana, Pachana, Kaphapittahara.',
    description: 'The iconic classical trifecta of myrobalans. Cleanses the entire alimentary canal, protects visual acuity (Chakshushya) and neutralizes free-radical oxidative stress.',
    imageUrl: 'https://images.unsplash.com/photo-1509316975850-ff9c5deb0cd9?w=600&auto=format&fit=crop&q=80',
    status: 'Active',
    manufacturer: 'Sitaram Ayurveda Pvt. Ltd.',
    featured: true,
    createdAt: '2026-08-06T08:30:00Z',
    updatedAt: '2026-09-13T10:20:00Z'
  },
  {
    id: 'prod_avipathy_choornam',
    code: 'SA-00007',
    name: 'Avipathy Choornam',
    category: 'Choornams',
    classicalReference: 'Ashtangahrudayam',
    packings: ['100 g'],
    ingredients: ['Sunthi (Zingiber officinale)', 'Maricha (Piper nigrum)', 'Pippali', 'Haritaki', 'Bibhitaki', 'Amalaki', 'Musta', 'Vidanga', 'Ela', 'Patra', 'Lavanga', 'Trivrit (Operculina turpethum)', 'Sharkara (Sugar)'],
    usage: '5 to 10 grams early morning on an empty stomach or at bedtime with warm water or honey.',
    indications: 'Amlapitta (Hyperacidity & acid reflux), Vibanda, Pitta vikara, Jwara, Chardi (Vomiting), Daha (Burning sensation), Prameha, Vishavikara.',
    description: 'Gentle, cooling Pitta-virechana powder. Neutralizes excess gastric acid and pacifies fiery burning sensations in the chest and stomach.',
    imageUrl: 'https://images.unsplash.com/photo-1509316975850-ff9c5deb0cd9?w=600&auto=format&fit=crop&q=80',
    status: 'Active',
    manufacturer: 'Sitaram Ayurveda Pvt. Ltd.',
    featured: false,
    createdAt: '2026-08-07T10:45:00Z',
    updatedAt: '2026-09-02T13:40:00Z'
  },
  {
    id: 'prod_kalyanaka_ghrutham',
    code: 'SA-00008',
    name: 'Kalyanaka Ghrutham',
    category: 'Ghruthams',
    classicalReference: 'Ashtangahrudayam (Uttarasthana)',
    packings: ['150 g', '250 g'],
    ingredients: ['Triphala', 'Haridra (Curcuma longa)', 'Daruharidra (Berberis aristata)', 'Saraswathi / Brahmi (Bacopa monnieri)', 'Sariva (Hemidesmus indicus)', 'Tagara', 'Go Ghrutha (Pure Cow Ghee)'],
    usage: '10 to 15 grams early morning on empty stomach with warm milk or lukewarm water.',
    indications: 'Unmada (Psychosis / Behavioral disturbances), Apasmara (Epilepsy), Bhuta graham, Smriti bhramsha (Memory impairment), Vandhyatva (Infertility), Mangala karaka.',
    description: 'Sacred classical medicated cow ghee indicated in cognitive dysfunctions, mood imbalances, seizure tendencies and reproductive rejuvenation.',
    imageUrl: 'https://images.unsplash.com/photo-1589927986089-35812388d1f4?w=600&auto=format&fit=crop&q=80',
    status: 'Active',
    manufacturer: 'Sitaram Ayurveda Pvt. Ltd.',
    featured: true,
    createdAt: '2026-08-08T15:00:00Z',
    updatedAt: '2026-09-09T18:00:00Z'
  },
  {
    id: 'prod_mahatiktaka_ghrutham',
    code: 'SA-00009',
    name: 'Mahatiktaka Ghrutham',
    category: 'Ghruthams',
    classicalReference: 'Bhaishajya Ratnavali',
    packings: ['150 g'],
    ingredients: ['Saptaparna (Alstonia scholaris)', 'Ativisha (Aconitum heterophyllum)', 'Aragvadha (Cassia fistula)', 'Katuki', 'Nimba (Azadirachta indica)', 'Go Ghrutha'],
    usage: '10 to 15 grams early morning on empty stomach with warm water.',
    indications: 'Kushtha (Chronic dermatological diseases, Psoriasis, Eczema), Visarpa (Erysipelas / Herpes), Rakta pitta (Bleeding disorders), Amavata, Trishna, Kamala (Jaundice).',
    description: 'Supreme bitter medicinal ghee for deep blood purification (Rakta Prasadana) and recalcitrant inflammatory skin lesions.',
    imageUrl: 'https://images.unsplash.com/photo-1589927986089-35812388d1f4?w=600&auto=format&fit=crop&q=80',
    status: 'Active',
    manufacturer: 'Sitaram Ayurveda Pvt. Ltd.',
    featured: false,
    createdAt: '2026-08-09T16:30:00Z',
    updatedAt: '2026-09-06T14:15:00Z'
  },
  {
    id: 'prod_dhanwantharam_thailam',
    code: 'SA-00010',
    name: 'Dhanwantharam Thailam',
    category: 'Thailams',
    classicalReference: 'Ashtangahrudayam',
    packings: ['200 ml', '450 ml', '1 L'],
    ingredients: ['Bala moola (Sida cordifolia)', 'Go Ksheera (Cow Milk)', 'Yava (Barley)', 'Kola (Ziziphus jujuba)', 'Kulattha (Horse gram)', 'Dashamoola', 'Tila Thailam (Pure Sesame Oil)'],
    usage: 'External application for whole body Abhyanga, local massage, Pichu, or internal Matra Vasthi under physician guidance.',
    indications: 'Sarva Vata roga (All 80 types of Vata neuro-muscular disorders), Pakshaghata (Hemiplegia / Stroke rehab), Suthika paricharya (Postnatal care), Bhagna (Fractures).',
    description: 'The foundational neuromuscular restorative oil of Kerala Ayurveda. Processed with Bala root and cow milk for deep musculoskeletal nourishment.',
    imageUrl: 'https://images.unsplash.com/photo-1608571423902-eed4a5ad8108?w=600&auto=format&fit=crop&q=80',
    status: 'Active',
    manufacturer: 'Sitaram Ayurveda Pvt. Ltd.',
    featured: true,
    createdAt: '2026-08-10T11:20:00Z',
    updatedAt: '2026-09-14T08:30:00Z'
  },
  {
    id: 'prod_murivenna',
    code: 'SA-00011',
    name: 'Murivenna',
    category: 'Kera Thailams',
    classicalReference: 'Traditional Kerala Sahasrayogam',
    packings: ['100 ml', '200 ml', '450 ml'],
    ingredients: ['Tambula (Piper betle)', 'Sigru patra (Moringa oleifera)', 'Parpata', 'Kanyaka / Kumari (Aloe vera)', 'Pyasya', 'Kera thailam (Cold pressed Coconut Oil)'],
    usage: 'External application over affected areas, wound dressing, Pichu on joints, or gentle massage.',
    indications: 'Vrana (Fresh cuts, lacerations, surgical wounds), Bhagna (Bone fractures, joint sprains), Burns, Contusions, Muscular stiffness, Post-traumatic inflammation.',
    description: 'Famous rapid wound-healing oil of Kerala tradition. Instantly relieves localized pain, prevents secondary skin infection and promotes tissue granulations.',
    imageUrl: 'https://images.unsplash.com/photo-1608571423902-eed4a5ad8108?w=600&auto=format&fit=crop&q=80',
    status: 'Active',
    manufacturer: 'Sitaram Ayurveda Pvt. Ltd.',
    featured: true,
    createdAt: '2026-08-11T13:40:00Z',
    updatedAt: '2026-09-13T12:00:00Z'
  },
  {
    id: 'prod_chyavanaprasam',
    code: 'SA-00012',
    name: 'Chyavanaprasam (Sitaram Special)',
    category: 'Lehyams',
    classicalReference: 'Charaka Samhita (Chikitsasthana)',
    packings: ['250 g', '500 g', '1 kg'],
    ingredients: ['Amalaki fresh fruit pulp (Emblica officinalis)', 'Dashamoola', 'Ashtavarga', 'Pippali', 'Guduchi', 'Bala', 'Tukakshiri', 'Madhu (Pure Honey)', 'Ghrutha (Pure Cow Ghee)'],
    usage: '10 to 15 grams morning and evening followed by a glass of warm milk, or as directed.',
    indications: 'Kasa, Shwasa, Kshaya, Daurbalya (Debility & fatigue), Ojas depletion, Recurrent seasonal infections, Premature aging, Loss of voice and stamina.',
    description: 'The timeless Ayurvedic elixir of youth and stamina. Formulated using fresh seasonal Amla fruits and over 48 wild-crafted Himalayan and Western Ghat botanicals.',
    imageUrl: 'https://images.unsplash.com/photo-1546868871-7041f2a55e12?w=600&auto=format&fit=crop&q=80',
    status: 'Active',
    manufacturer: 'Sitaram Ayurveda Pvt. Ltd.',
    featured: true,
    createdAt: '2026-08-12T09:00:00Z',
    updatedAt: '2026-09-14T09:15:00Z'
  },
  {
    id: 'prod_yogaraja_guggulu',
    code: 'SA-00013',
    name: 'Yogaraja Guggulu Tablet',
    category: 'Gulika, Gulika Tablets, Capsules',
    classicalReference: 'Bhaishajya Ratnavali',
    packings: ['60 Tablets', '100 Tablets'],
    ingredients: ['Shuddha Guggulu (Commiphora mukul)', 'Chitraka (Plumbago zeylanica)', 'Pippalimoola', 'Yavani (Trachyspermum ammi)', 'Ajamoda', 'Trikatu', 'Triphala'],
    usage: '1 to 2 tablets twice daily with warm water or Maharasnadi Kashayam after food.',
    indications: 'Sandhivata (Osteoarthritis), Amavata (Rheumatoid arthritis), Gout, Vata-rakta, Sciatica, Chronic back stiffness, Joint swelling.',
    description: 'Potent synergistic Guggulu compound for dispelling metabolic toxin (Ama) from joints, relieving pain and restoring joint lubrication.',
    imageUrl: 'https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?w=600&auto=format&fit=crop&q=80',
    status: 'Active',
    manufacturer: 'Sitaram Ayurveda Pvt. Ltd.',
    featured: false,
    createdAt: '2026-08-13T10:15:00Z',
    updatedAt: '2026-09-07T11:00:00Z'
  },
  {
    id: 'prod_maharasnadi_kashayam_tab',
    code: 'SA-00014',
    name: 'Maharasnadi Kashayam Tablet',
    category: 'Kashayam Tablets',
    classicalReference: 'Sahasrayogam',
    packings: ['60 Tablets', '100 Tablets'],
    ingredients: ['Rasna (Pluchea lanceolata)', 'Dhanvayasa', 'Bala', 'Eranda moola (Ricinus communis)', 'Devadaru (Cedrus deodara)', 'Shati', 'Guduchi'],
    usage: '2 tablets twice daily on empty stomach with warm water, 30 minutes before meals.',
    indications: 'Pakshaghata (Hemiplegia), Manyastambha (Cervical spondylosis), Gridhrasi (Sciatica), Paraplegia, Facial palsy, Chronic arthritic syndromes.',
    description: 'High-potency Kashayam tablet capturing the full therapeutic bioactives of classical 26-herb Maharasnadi decoction without liquid bitterness.',
    imageUrl: 'https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?w=600&auto=format&fit=crop&q=80',
    status: 'Active',
    manufacturer: 'Sitaram Ayurveda Pvt. Ltd.',
    featured: true,
    createdAt: '2026-08-14T14:50:00Z',
    updatedAt: '2026-09-12T17:20:00Z'
  },
  {
    id: 'prod_sahacharadi_kuzhambu',
    code: 'SA-00015',
    name: 'Sahacharadi Kuzhambu',
    category: 'Kuzhambus',
    classicalReference: 'Ashtangahrudayam',
    packings: ['200 ml', '450 ml'],
    ingredients: ['Sahachara (Strobilanthes ciliatus)', 'Dashamoola', 'Abhiru (Asparagus racemosus)', 'Tila Thailam', 'Eranda Thailam', 'Go Ghrutha'],
    usage: 'External application with warm gentle stroking over lower limbs from distal to proximal.',
    indications: 'Gridhrasi (Sciatica), Varicose veins, Intermittent claudication, Tremors, Numbness of lower extremities, Muscular atrophy.',
    description: 'Rich triple-fat Kuzhambu formulation specifically designed to strengthen venous return and soothe aggravated Vata in pelvic and lower limb nerves.',
    imageUrl: 'https://images.unsplash.com/photo-1608571423902-eed4a5ad8108?w=600&auto=format&fit=crop&q=80',
    status: 'Active',
    manufacturer: 'Sitaram Ayurveda Pvt. Ltd.',
    featured: false,
    createdAt: '2026-08-15T09:30:00Z',
    updatedAt: '2026-09-04T12:10:00Z'
  },
  {
    id: 'prod_kumkumadi_tailam',
    code: 'SA-00016',
    name: 'Kumkumadi Tailam (Kashmiri Saffron Elixir)',
    category: 'Thailams',
    classicalReference: 'Ashtangahrudayam & Bhaishajya Ratnavali',
    packings: ['15 ml', '30 ml'],
    ingredients: ['Kunkuma / Saffron (Crocus sativus)', 'Chandana (Santalum album)', 'Manjistha', 'Yashtimadhu', 'Padmaka', 'Go Ksheera', 'Tila Thailam'],
    usage: 'Apply 3 to 4 drops on clean face and gently massage in upward strokes before bedtime.',
    indications: 'Vyanga (Hyperpigmentation, Dark spots), Nilika (Blemishes), Yuvana pidaka (Acne marks), Dull complexion, Fine dry wrinkles.',
    description: 'Exquisite classical micro-processed facial oil infused with pure grade-A Kashmiri saffron for illuminated skin tone and cellular renewal.',
    imageUrl: 'https://images.unsplash.com/photo-1608571423902-eed4a5ad8108?w=600&auto=format&fit=crop&q=80',
    status: 'Active',
    manufacturer: 'Sitaram Ayurveda Pvt. Ltd.',
    featured: true,
    createdAt: '2026-08-16T16:00:00Z',
    updatedAt: '2026-09-14T07:45:00Z'
  },
  {
    id: 'prod_brahmi_capsules',
    code: 'SA-00017',
    name: 'Single Herb Brahmi Veg Capsules',
    category: 'Single Herb Veg Capsules',
    classicalReference: 'Ayurvedic Pharmacopoeia of India (API)',
    packings: ['60 Capsules'],
    ingredients: ['Brahmi standardized extract (Bacopa monnieri - 20% Bacosides)'],
    usage: '1 capsule twice daily with warm milk or water after food.',
    indications: 'Smriti kshaya (Impaired memory), Medha kshaya (Reduced cognition & mental stamina), Mental fatigue, Exam stress, Anxiety, ADHD support.',
    description: 'Pure bio-active Bacoside extract delivering targeted neuroprotection, synaptic plasticity and serene mental clarity in vegetarian capsules.',
    imageUrl: 'https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?w=600&auto=format&fit=crop&q=80',
    status: 'Active',
    manufacturer: 'Sitaram Ayurveda Pvt. Ltd.',
    featured: false,
    createdAt: '2026-08-17T11:10:00Z',
    updatedAt: '2026-09-01T15:00:00Z'
  },
  {
    id: 'prod_ksheerabala_avarti',
    code: 'SA-00018',
    name: 'Ksheerabala (101 Avarti) Soft Gel',
    category: 'Soft Gel Capsules',
    classicalReference: 'Ashtangahrudayam',
    packings: ['30 Softgels', '100 Softgels'],
    ingredients: ['Bala moola (Sida cordifolia)', 'Go Ksheera (Cow Milk)', 'Tila Thailam - Processed 101 cycles'],
    usage: '1 to 2 softgels twice daily with warm water or milk, or as directed by the physician.',
    indications: 'Vatarakta (Gout), Facial palsy, Trigeminal neuralgia, Insomnia, Peripheral neuropathy, Degenerative disc disease, Sensory motor loss.',
    description: 'Sublime ultra-potentiated preparation cooked 101 consecutive times for instantaneous cellular permeability across the blood-brain barrier.',
    imageUrl: 'https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?w=600&auto=format&fit=crop&q=80',
    status: 'Active',
    manufacturer: 'Sitaram Ayurveda Pvt. Ltd.',
    featured: true,
    createdAt: '2026-08-18T10:00:00Z',
    updatedAt: '2026-09-11T13:30:00Z'
  },
  {
    id: 'prod_swarnabhasma',
    code: 'SA-00019',
    name: 'Swarna Bhasma (Micro-fine Gold Ash)',
    category: 'Bhasmas / Ksharams',
    classicalReference: 'Rasa Tarangini & Rasaratna Samuchaya',
    packings: ['100 mg', '500 mg', '1 g'],
    ingredients: ['Shuddha Swarna (24 Karat Purified Gold)', 'Parada', 'Gandhaka', 'Kumari swarasa'],
    usage: '15 to 30 mg mixed with fresh butter or raw honey under strict Ayurvedic medical supervision.',
    indications: 'Autoimmune deficiencies, Rajayakshma, Severe physical & cognitive wasting, Hemiplegia, Unmada, Chronic cardiac debility, Longevity Rasayana.',
    description: 'Sacred Ayurvedic incinerated nano-gold calx prepared through traditional puta cycles. Acts as an unfailing catalyst (Yogavahi) and immunomodulator.',
    imageUrl: 'https://images.unsplash.com/photo-1509316975850-ff9c5deb0cd9?w=600&auto=format&fit=crop&q=80',
    status: 'Active',
    manufacturer: 'Sitaram Ayurveda Pvt. Ltd.',
    featured: false,
    createdAt: '2026-08-19T13:00:00Z',
    updatedAt: '2026-09-03T16:45:00Z'
  },
  {
    id: 'prod_sitaram_balm',
    code: 'SA-00020',
    name: 'Sitaram Herbal Pain Balm',
    category: 'O.T.C Products',
    classicalReference: 'Proprietary Sitaram Heritage Formulation',
    packings: ['10 g', '25 g', '50 g'],
    ingredients: ['Pudina satwa (Menthol)', 'Karpura satwa (Camphor)', 'Gandhapura thailam (Wintergreen oil)', 'Nilgiri thailam (Eucalyptus oil)', 'Lavanga thailam (Clove oil)', 'Bee wax base'],
    usage: 'Apply gently over temples for headache, or over neck, shoulders, and chest for cold and muscular tension.',
    indications: 'Shirashoola (Headache, Migraine aura), Pratishyaya (Common cold & nasal blockage), Joint stiffness, Muscular sprain.',
    description: 'Fast-acting aromatic soothing balm formulated with pure essential oils for immediate relief from tension headaches and cold congestion.',
    imageUrl: 'https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?w=600&auto=format&fit=crop&q=80',
    status: 'Active',
    manufacturer: 'Sitaram Ayurveda Pvt. Ltd.',
    featured: true,
    createdAt: '2026-08-20T17:00:00Z',
    updatedAt: '2026-09-14T06:00:00Z'
  },
  {
    id: 'prod_agasthya_rasayanam',
    code: 'SA-00021',
    name: 'Agasthya Rasayanam',
    category: 'Lehyams',
    classicalReference: 'Ashtangahrudayam (Chikitsasthana)',
    packings: ['250 g', '500 g'],
    ingredients: ['Dashamoola', 'Yava (Barley)', 'Bala', 'Haritaki', 'Chitraka', 'Pippali', 'Guda', 'Madhu', 'Ghrutha'],
    usage: '10 to 15 grams twice daily after food followed by lukewarm water.',
    indications: 'Kasa (Chronic bronchitis), Shwasa (Asthma), Hikka (Hiccups), Swarabheda (Hoarseness of voice), Kshaya, Mandagni.',
    description: 'Sage Agasthya classical lung-rejuvenating confection. Tones the broncho-alveolar lining and dispels deep-seated Kapha phlegm.',
    imageUrl: 'https://images.unsplash.com/photo-1546868871-7041f2a55e12?w=600&auto=format&fit=crop&q=80',
    status: 'Inactive',
    manufacturer: 'Sitaram Ayurveda Pvt. Ltd.',
    featured: false,
    createdAt: '2026-08-21T08:00:00Z',
    updatedAt: '2026-09-12T11:00:00Z'
  }
];

// Normalized Master Ingredients Registry
const DEFAULT_INGREDIENTS = [
  { id: 'ing_abhaya', name: 'Abhaya', botanicalName: 'Terminalia chebula', sanskritName: 'अभया (हरीतकी)', partUsed: 'Fruit pericarp', productsCount: 4, status: 'Active' },
  { id: 'ing_dhatri', name: 'Dhatri / Amalaki', botanicalName: 'Emblica officinalis', sanskritName: 'धात्री (आमलकी)', partUsed: 'Fresh & dried fruit', productsCount: 5, status: 'Active' },
  { id: 'ing_guduchi', name: 'Amrutha / Guduchi', botanicalName: 'Tinospora cordifolia', sanskritName: 'अमृता (गुडूची)', partUsed: 'Mature stem', productsCount: 4, status: 'Active' },
  { id: 'ing_ashwagandha', name: 'Ashwagandha', botanicalName: 'Withania somnifera', sanskritName: 'अश्वगंधा', partUsed: 'Root', productsCount: 3, status: 'Active' },
  { id: 'ing_dashamoola', name: 'Dashamoola', botanicalName: 'Ten Sacred Roots Compound', sanskritName: 'दशमूल', partUsed: 'Roots of 10 trees/herbs', productsCount: 5, status: 'Active' },
  { id: 'ing_bala', name: 'Bala', botanicalName: 'Sida cordifolia', sanskritName: 'बला', partUsed: 'Root and whole plant', productsCount: 4, status: 'Active' },
  { id: 'ing_guggulu', name: 'Guggulu (Purified)', botanicalName: 'Commiphora mukul', sanskritName: 'शुद्ध गुग्गुलु', partUsed: 'Purified gum resin', productsCount: 3, status: 'Active' },
  { id: 'ing_draksha', name: 'Draksha', botanicalName: 'Vitis vinifera', sanskritName: 'द्राक्षा', partUsed: 'Dried sweet fruit', productsCount: 2, status: 'Active' },
  { id: 'ing_brahmi', name: 'Brahmi', botanicalName: 'Bacopa monnieri', sanskritName: 'ब्राह्मी', partUsed: 'Whole plant', productsCount: 3, status: 'Active' },
  { id: 'ing_saffron', name: 'Kunkuma (Saffron)', botanicalName: 'Crocus sativus', sanskritName: 'कुंकुम (केसर)', partUsed: 'Stigma & styles', productsCount: 2, status: 'Active' },
  { id: 'ing_triphala', name: 'Triphala Compound', botanicalName: 'Tri-myrobalan compound', sanskritName: 'त्रिफला', partUsed: 'Three fruits equal ratio', productsCount: 6, status: 'Active' },
  { id: 'ing_rasna', name: 'Rasna', botanicalName: 'Pluchea lanceolata', sanskritName: 'रास्ना', partUsed: 'Root / Rhizome', productsCount: 3, status: 'Active' },
  { id: 'ing_swarna', name: 'Swarna Bhasma', botanicalName: 'Aurum nano-calx', sanskritName: 'स्वर्ण भस्म', partUsed: 'Processed incinerated gold', productsCount: 1, status: 'Active' },
  { id: 'ing_arjuna', name: 'Arjuna', botanicalName: 'Terminalia arjuna', sanskritName: 'अर्जुन', partUsed: 'Stem bark', productsCount: 2, status: 'Active' }
];

// App Content: Banners for Future Android Application
const DEFAULT_BANNERS = [
  {
    id: 'ban_01',
    title: 'Monsoon Karkidaka Chikitsa',
    subtitle: 'Revitalizing classical rasayanas and medicated tailams for seasonal immunity',
    imageUrl: 'https://images.unsplash.com/photo-1544367567-0f2fcb009e0b?w=1200&auto=format&fit=crop&q=80',
    targetType: 'Category',
    targetValue: 'Lehyams',
    status: 'Active',
    order: 1
  },
  {
    id: 'ban_02',
    title: 'Kerala Classical Taila Heritage',
    subtitle: 'Centuries-tested neuromuscular restoration with Bala, Sahachara & Dhanwantharam',
    imageUrl: 'https://images.unsplash.com/photo-1608571423902-eed4a5ad8108?w=1200&auto=format&fit=crop&q=80',
    targetType: 'Category',
    targetValue: 'Thailams',
    status: 'Active',
    order: 2
  }
];

// App Content: Mobile Announcements
const DEFAULT_ANNOUNCEMENTS = [
  {
    id: 'ann_01',
    title: 'Fresh Seasonal Amalaki Batch Harvested',
    message: 'All Chyavanaprasam batches are currently being cooked using fresh Western Ghats winter Amalaki pulp.',
    priority: 'Normal',
    status: 'Active',
    date: '2026-09-12'
  },
  {
    id: 'ann_02',
    title: 'Ayush GMP Certification Renewed',
    message: 'Thrissur manufacturing division quality laboratory renewed with zero non-conformances.',
    priority: 'High',
    status: 'Active',
    date: '2026-09-01'
  }
];

// Initial Audit Logs
const DEFAULT_AUDIT = [
  { id: 'aud_01', timestamp: '2026-09-14 09:15:22', admin: 'admin@sitaramayurveda.com', action: 'PRODUCT_UPDATE', entity: 'SA-00012 (Chyavanaprasam)', details: 'Updated packing sizes to include 1 kg economy tub.' },
  { id: 'aud_02', timestamp: '2026-09-14 07:45:10', admin: 'admin@sitaramayurveda.com', action: 'PRODUCT_UPDATE', entity: 'SA-00016 (Kumkumadi Tailam)', details: 'Refreshed posology guidelines and high-res dossier image.' },
  { id: 'aud_03', timestamp: '2026-09-13 14:20:00', admin: 'admin@sitaramayurveda.com', action: 'STATUS_TOGGLE', entity: 'SA-00021 (Agasthya Rasayanam)', details: 'Product set to Inactive pending fresh batch release.' },
  { id: 'aud_04', timestamp: '2026-09-12 11:30:15', admin: 'admin@sitaramayurveda.com', action: 'CATEGORY_VERIFIED', entity: 'All 24 Handbook Categories', details: 'Full alignment verified with Sitaram Therapeutic Index Handbook.' }
];

const DEFAULT_SETTINGS = {
  centralDbConnected: true,
  firestoreProjectId: 'sitaram-ayurveda-central-prod',
  firestoreApiKey: 'AIzaSyA8B9C0D1E2F3G4H5I6J7K8L9M0N1P2Q3R',
  syncStatus: 'Synchronized with Central Cloud Firestore',
  lastSyncTime: new Date().toISOString(),
  autoSyncIntervalMinutes: 5,
  adminNotificationEmail: 'admin@sitaramayurveda.com',
  catalogueVersion: '2026.4 — Therapeutic Index Edition'
};

/**
 * DB Manager Singleton
 */
class SitaramDB {
  constructor() {
    this.init();
  }

  init() {
    if (!localStorage.getItem(DB_KEY_CATEGORIES)) {
      localStorage.setItem(DB_KEY_CATEGORIES, JSON.stringify(DEFAULT_CATEGORIES));
    }
    if (!localStorage.getItem(DB_KEY_PRODUCTS)) {
      localStorage.setItem(DB_KEY_PRODUCTS, JSON.stringify(DEFAULT_PRODUCTS));
    }
    if (!localStorage.getItem(DB_KEY_INGREDIENTS)) {
      localStorage.setItem(DB_KEY_INGREDIENTS, JSON.stringify(DEFAULT_INGREDIENTS));
    }
    if (!localStorage.getItem(DB_KEY_MANUFACTURERS)) {
      localStorage.setItem(DB_KEY_MANUFACTURERS, JSON.stringify(DEFAULT_MANUFACTURERS));
    }
    if (!localStorage.getItem(DB_KEY_BANNERS)) {
      localStorage.setItem(DB_KEY_BANNERS, JSON.stringify(DEFAULT_BANNERS));
    }
    if (!localStorage.getItem(DB_KEY_ANNOUNCEMENTS)) {
      localStorage.setItem(DB_KEY_ANNOUNCEMENTS, JSON.stringify(DEFAULT_ANNOUNCEMENTS));
    }
    if (!localStorage.getItem(DB_KEY_AUDIT)) {
      localStorage.setItem(DB_KEY_AUDIT, JSON.stringify(DEFAULT_AUDIT));
    }
    if (!localStorage.getItem(DB_KEY_SETTINGS)) {
      localStorage.setItem(DB_KEY_SETTINGS, JSON.stringify(DEFAULT_SETTINGS));
    }
    this.recalculateCounts();
  }

  // Categories
  getCategories() {
    return JSON.parse(localStorage.getItem(DB_KEY_CATEGORIES) || '[]');
  }
  saveCategories(cats) {
    localStorage.setItem(DB_KEY_CATEGORIES, JSON.stringify(cats));
  }
  addCategory(cat) {
    const cats = this.getCategories();
    cat.id = 'cat_' + Date.now();
    cat.productCount = 0;
    cats.push(cat);
    this.saveCategories(cats);
    this.logAudit('CATEGORY_ADD', cat.name, `Created category "${cat.name}" [${cat.code}]`);
    return cat;
  }
  updateCategory(id, updated) {
    const cats = this.getCategories();
    const idx = cats.findIndex(c => c.id === id);
    if (idx !== -1) {
      cats[idx] = { ...cats[idx], ...updated };
      this.saveCategories(cats);
      this.logAudit('CATEGORY_UPDATE', cats[idx].name, `Updated details for category "${cats[idx].name}"`);
      return cats[idx];
    }
    return null;
  }
  deleteCategory(id) {
    const products = this.getProducts();
    const cat = this.getCategories().find(c => c.id === id);
    if (!cat) return { success: false, message: 'Category not found' };

    const linkedProducts = products.filter(p => p.category === cat.name);
    if (linkedProducts.length > 0) {
      return { 
        success: false, 
        message: `Cannot delete category "${cat.name}". It is assigned to ${linkedProducts.length} product(s). Please reassign them first.` 
      };
    }
    const filtered = this.getCategories().filter(c => c.id !== id);
    this.saveCategories(filtered);
    this.logAudit('CATEGORY_DELETE', cat.name, `Deleted category "${cat.name}"`);
    return { success: true };
  }

  // Products
  getProducts() {
    return JSON.parse(localStorage.getItem(DB_KEY_PRODUCTS) || '[]');
  }
  saveProducts(prods) {
    localStorage.setItem(DB_KEY_PRODUCTS, JSON.stringify(prods));
    this.recalculateCounts();
  }
  getProductById(id) {
    return this.getProducts().find(p => p.id === id || p.code === id);
  }
  addProduct(prod) {
    const prods = this.getProducts();
    // Validate unique code
    if (prods.some(p => p.code.toLowerCase() === prod.code.toLowerCase())) {
      throw new Error(`Product Code "${prod.code}" is already in use. Please enter a unique code.`);
    }
    prod.id = 'prod_' + Date.now();
    prod.createdAt = new Date().toISOString();
    prod.updatedAt = prod.createdAt;
    prods.unshift(prod);
    this.saveProducts(prods);
    this.logAudit('PRODUCT_ADD', `${prod.code} (${prod.name})`, `Added new product to category "${prod.category}"`);
    return prod;
  }
  updateProduct(id, updated) {
    const prods = this.getProducts();
    const idx = prods.findIndex(p => p.id === id);
    if (idx === -1) throw new Error('Product not found.');

    // Check code collision if code changed
    if (updated.code && updated.code.toLowerCase() !== prods[idx].code.toLowerCase()) {
      if (prods.some((p, i) => i !== idx && p.code.toLowerCase() === updated.code.toLowerCase())) {
        throw new Error(`Product Code "${updated.code}" is already taken by another product.`);
      }
    }

    prods[idx] = { 
      ...prods[idx], 
      ...updated, 
      updatedAt: new Date().toISOString() 
    };
    this.saveProducts(prods);
    this.logAudit('PRODUCT_UPDATE', `${prods[idx].code} (${prods[idx].name})`, `Updated formulation details and posology.`);
    return prods[idx];
  }
  toggleProductStatus(id) {
    const prods = this.getProducts();
    const p = prods.find(x => x.id === id);
    if (!p) return null;
    p.status = p.status === 'Active' ? 'Inactive' : 'Active';
    p.updatedAt = new Date().toISOString();
    this.saveProducts(prods);
    this.logAudit('STATUS_TOGGLE', `${p.code} (${p.name})`, `Status changed to ${p.status}`);
    return p;
  }
  deleteProduct(id) {
    const prods = this.getProducts();
    const p = prods.find(x => x.id === id);
    if (!p) return false;
    const filtered = prods.filter(x => x.id !== id);
    this.saveProducts(filtered);
    this.logAudit('PRODUCT_DELETE', `${p.code} (${p.name})`, `Permanently deleted product from catalogue.`);
    return true;
  }

  // Bulk Operations
  bulkUpdateStatus(ids, newStatus) {
    const prods = this.getProducts();
    let count = 0;
    prods.forEach(p => {
      if (ids.includes(p.id)) {
        p.status = newStatus;
        p.updatedAt = new Date().toISOString();
        count++;
      }
    });
    this.saveProducts(prods);
    this.logAudit('BULK_STATUS', `${count} Products`, `Bulk set status to ${newStatus}`);
    return count;
  }
  bulkChangeCategory(ids, newCategory) {
    const prods = this.getProducts();
    let count = 0;
    prods.forEach(p => {
      if (ids.includes(p.id)) {
        p.category = newCategory;
        p.updatedAt = new Date().toISOString();
        count++;
      }
    });
    this.saveProducts(prods);
    this.logAudit('BULK_CATEGORY', `${count} Products`, `Bulk moved to category "${newCategory}"`);
    return count;
  }
  bulkDelete(ids) {
    const prods = this.getProducts();
    const count = ids.length;
    const filtered = prods.filter(p => !ids.includes(p.id));
    this.saveProducts(filtered);
    this.logAudit('BULK_DELETE', `${count} Products`, `Bulk deleted ${count} products.`);
    return count;
  }

  // Ingredients
  getIngredients() {
    return JSON.parse(localStorage.getItem(DB_KEY_INGREDIENTS) || '[]');
  }
  saveIngredients(ings) {
    localStorage.setItem(DB_KEY_INGREDIENTS, JSON.stringify(ings));
  }
  addIngredient(ing) {
    const ings = this.getIngredients();
    ing.id = 'ing_' + Date.now();
    ing.productsCount = 0;
    ings.push(ing);
    this.saveIngredients(ings);
    this.logAudit('INGREDIENT_ADD', ing.name, `Registered new botanical ingredient: ${ing.name} (${ing.botanicalName})`);
    return ing;
  }
  updateIngredient(id, updated) {
    const ings = this.getIngredients();
    const idx = ings.findIndex(i => i.id === id);
    if (idx !== -1) {
      ings[idx] = { ...ings[idx], ...updated };
      this.saveIngredients(ings);
      this.logAudit('INGREDIENT_UPDATE', ings[idx].name, `Updated botanical metadata for ${ings[idx].name}`);
      return ings[idx];
    }
    return null;
  }
  getProductsForIngredient(ingredientName) {
    const search = ingredientName.toLowerCase().trim();
    return this.getProducts().filter(p => {
      return (p.ingredients || []).some(ing => ing.toLowerCase().includes(search));
    });
  }

  // Manufacturers
  getManufacturers() {
    return JSON.parse(localStorage.getItem(DB_KEY_MANUFACTURERS) || '[]');
  }
  saveManufacturers(mfgs) {
    localStorage.setItem(DB_KEY_MANUFACTURERS, JSON.stringify(mfgs));
  }
  addManufacturer(mfg) {
    const mfgs = this.getManufacturers();
    mfg.id = 'mfg_' + Date.now();
    mfgs.push(mfg);
    this.saveManufacturers(mfgs);
    this.logAudit('MANUFACTURER_ADD', mfg.name, `Added manufacturing unit: ${mfg.name}`);
    return mfg;
  }

  // App Content (Banners, Announcements)
  getBanners() {
    return JSON.parse(localStorage.getItem(DB_KEY_BANNERS) || '[]');
  }
  saveBanners(b) {
    localStorage.setItem(DB_KEY_BANNERS, JSON.stringify(b));
  }
  getAnnouncements() {
    return JSON.parse(localStorage.getItem(DB_KEY_ANNOUNCEMENTS) || '[]');
  }
  saveAnnouncements(a) {
    localStorage.setItem(DB_KEY_ANNOUNCEMENTS, JSON.stringify(a));
  }

  // Audit Logs
  getAuditLogs() {
    return JSON.parse(localStorage.getItem(DB_KEY_AUDIT) || '[]');
  }
  logAudit(action, entity, details) {
    const logs = this.getAuditLogs();
    const currentAdmin = localStorage.getItem('sitaram_admin_email') || 'admin@sitaramayurveda.com';
    const now = new Date();
    const pad = (n) => String(n).padStart(2, '0');
    const ts = `${now.getFullYear()}-${pad(now.getMonth()+1)}-${pad(now.getDate())} ${pad(now.getHours())}:${pad(now.getMinutes())}:${pad(now.getSeconds())}`;
    
    logs.unshift({
      id: 'aud_' + Date.now() + Math.random().toString(36).substr(2, 4),
      timestamp: ts,
      admin: currentAdmin,
      action,
      entity,
      details
    });
    // Keep max 250 records
    if (logs.length > 250) logs.length = 250;
    localStorage.setItem(DB_KEY_AUDIT, JSON.stringify(logs));
  }

  // Settings
  getSettings() {
    return JSON.parse(localStorage.getItem(DB_KEY_SETTINGS) || JSON.stringify(DEFAULT_SETTINGS));
  }
  saveSettings(s) {
    localStorage.setItem(DB_KEY_SETTINGS, JSON.stringify(s));
    this.logAudit('SETTINGS_UPDATE', 'System Configuration', 'Updated central database & synchronization parameters.');
  }

  // Reset to Factory Handbook Defaults
  resetToFactoryDefaults() {
    localStorage.setItem(DB_KEY_CATEGORIES, JSON.stringify(DEFAULT_CATEGORIES));
    localStorage.setItem(DB_KEY_PRODUCTS, JSON.stringify(DEFAULT_PRODUCTS));
    localStorage.setItem(DB_KEY_INGREDIENTS, JSON.stringify(DEFAULT_INGREDIENTS));
    localStorage.setItem(DB_KEY_MANUFACTURERS, JSON.stringify(DEFAULT_MANUFACTURERS));
    localStorage.setItem(DB_KEY_BANNERS, JSON.stringify(DEFAULT_BANNERS));
    localStorage.setItem(DB_KEY_ANNOUNCEMENTS, JSON.stringify(DEFAULT_ANNOUNCEMENTS));
    localStorage.setItem(DB_KEY_SETTINGS, JSON.stringify(DEFAULT_SETTINGS));
    this.recalculateCounts();
    this.logAudit('FACTORY_RESET', 'Catalogue Database', 'Reset entire catalogue to Sitaram Therapeutic Index Handbook defaults.');
  }

  // Count recalculations
  recalculateCounts() {
    const prods = this.getProducts();
    const cats = this.getCategories();
    cats.forEach(c => {
      c.productCount = prods.filter(p => p.category === c.name).length;
    });
    this.saveCategories(cats);

    const ings = this.getIngredients();
    ings.forEach(i => {
      const s = i.name.toLowerCase().trim();
      i.productsCount = prods.filter(p => (p.ingredients || []).some(item => item.toLowerCase().includes(s))).length;
    });
    this.saveIngredients(ings);
  }

  // Suggest Next Code
  getNextProductCode() {
    const prods = this.getProducts();
    let max = 0;
    prods.forEach(p => {
      const match = p.code.match(/SA-(\d+)/i);
      if (match) {
        const n = parseInt(match[1], 10);
        if (n > max) max = n;
      }
    });
    return `SA-${String(max + 1).padStart(5, '0')}`;
  }
}

window.SitaramDB = new SitaramDB();
